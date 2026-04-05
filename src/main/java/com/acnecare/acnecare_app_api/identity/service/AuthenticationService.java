package com.acnecare.acnecare_app_api.identity.service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.acnecare.acnecare_app_api.common.exception.AppException;
import com.acnecare.acnecare_app_api.common.exception.ErrorCode;
import com.acnecare.acnecare_app_api.identity.dto.request.AuthenticationRequest;
import com.acnecare.acnecare_app_api.identity.dto.response.AuthenticationResponse;
import com.acnecare.acnecare_app_api.identity.dto.response.IntrospectResponse;
import com.acnecare.acnecare_app_api.identity.entity.InvalidatedToken;
import com.acnecare.acnecare_app_api.identity.entity.User;
import com.acnecare.acnecare_app_api.identity.repository.InvalidatedRepository;
import com.acnecare.acnecare_app_api.identity.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String signerKey;

    @NonFinal
    @Value("${jwt.access-token-valid-duration}")
    protected long accessTokenValidDuration;

    @NonFinal
    @Value("${jwt.refresh-token-valid-duration}")
    protected long refreshTokenValidDuration;

    UserRepository userRepository;
    InvalidatedRepository invalidatedRepository;
    AuthCookieService authCookieService;
    PasswordEncoder passwordEncoder;
    TokenRevocationService tokenRevocationService;

    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request, HttpServletResponse response)
            throws JOSEException, ParseException, AppException {

        User user = userRepository
                .findByEmailWithRoles(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if ("BLOCK".equals(user.getStatus())) {
            throw new AppException(ErrorCode.USER_IS_BLOCKED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        authCookieService.setAccessToken(response, accessToken);
        authCookieService.setRefreshToken(response, refreshToken);

        return AuthenticationResponse.builder().isAuthenticated(true).build();
    }

    @Transactional
    public AuthenticationResponse refreshToken(HttpServletResponse response, HttpServletRequest request)
            throws JOSEException, ParseException, AppException {

        String refreshRaw = authCookieService.readRefreshToken(request);
        if (refreshRaw == null || refreshRaw.isBlank()) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        var signedJWT = verifyRefreshToken(refreshRaw);
        var jti = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var userId = signedJWT.getJWTClaimsSet().getSubject();

        User user = userRepository.findByIdWithRoles(userId).orElse(null);
        if (user == null) {
            tokenRevocationService.revokeImmediately(jti, expiryTime);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if ("BLOCK".equals(user.getStatus())) {
            tokenRevocationService.revokeImmediately(jti, expiryTime);
            throw new AppException(ErrorCode.USER_IS_BLOCKED);
        }

        invalidatedRepository.save(
                InvalidatedToken.builder().id(jti).expiryTime(expiryTime).build());

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        authCookieService.setAccessToken(response, accessToken);
        authCookieService.setRefreshToken(response, refreshToken);

        return AuthenticationResponse.builder().isAuthenticated(true).build();
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response)
            throws JOSEException, ParseException, AppException {

        String accessRaw = authCookieService.readAccessToken(request);
        if (accessRaw != null && !accessRaw.isBlank()) {
            try {
                var signedAccessToken = verifyAccessToken(accessRaw);
                var jtiAccessToken = signedAccessToken.getJWTClaimsSet().getJWTID();
                invalidatedRepository.save(InvalidatedToken.builder()
                        .id(jtiAccessToken)
                        .expiryTime(signedAccessToken.getJWTClaimsSet().getExpirationTime())
                        .build());
            } catch (AppException ignored) {
            }
        }

        String refreshRaw = authCookieService.readRefreshToken(request);
        if (refreshRaw != null && !refreshRaw.isBlank()) {
            try {
                var signedRefreshToken = verifyRefreshToken(refreshRaw);
                var jtiRefreshToken = signedRefreshToken.getJWTClaimsSet().getJWTID();
                invalidatedRepository.save(InvalidatedToken.builder()
                        .id(jtiRefreshToken)
                        .expiryTime(signedRefreshToken.getJWTClaimsSet().getExpirationTime())
                        .build());
            } catch (AppException ignored) {
            }
        }

        authCookieService.deleteAccessToken(response);
        authCookieService.deleteRefreshToken(response);
    }

    public IntrospectResponse introspect(HttpServletRequest request) throws JOSEException, ParseException {

        var accessToken = authCookieService.readAccessToken(request);
        if (accessToken == null || accessToken.isBlank()) {
            return IntrospectResponse.builder().valid(false).build();
        }

        boolean isValid = true;

        try {
            verifyAccessToken(accessToken);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    private SignedJWT verifyAccessToken(String token) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(signerKey.getBytes(StandardCharsets.UTF_8));

        SignedJWT signedJWT = SignedJWT.parse(token);

        var verified = signedJWT.verify(verifier);
        if (!verified) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiryTime == null || !expiryTime.after(new Date())) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        if (invalidatedRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        if (!"access".equals(signedJWT.getJWTClaimsSet().getStringClaim("type"))) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        return signedJWT;
    }

    private SignedJWT verifyRefreshToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes(StandardCharsets.UTF_8));

        SignedJWT signedJWT = SignedJWT.parse(token);

        var verified = signedJWT.verify(verifier);
        if (!verified) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiryTime == null || !expiryTime.after(new Date())) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        if (invalidatedRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        if (!"refresh".equals(signedJWT.getJWTClaimsSet().getStringClaim("type"))) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        return signedJWT;
    }

    private String generateRefreshToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("acnecare")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(refreshTokenValidDuration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("type", "refresh")
                .build();

        Payload payload = new Payload(claims.toJSONObject());
        JWSObject jwtObject = new JWSObject(header, payload);

        try {
            jwtObject.sign(new MACSigner(signerKey.getBytes(StandardCharsets.UTF_8)));
            return jwtObject.serialize();

        } catch (JOSEException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_ERROR);
        }
    }

    private String generateAccessToken(User user) {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("acnecare")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(accessTokenValidDuration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("roles", buildScope(user))
                .claim("type", "access")
                .build();

        Payload payload = new Payload(claims.toJSONObject());

        JWSObject jwtObject = new JWSObject(header, payload);

        try {
            jwtObject.sign(new MACSigner(signerKey.getBytes(StandardCharsets.UTF_8)));
            return jwtObject.serialize();

        } catch (JOSEException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_ERROR);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> stringJoiner.add("ROLE_" + role.getName()));
        }

        return stringJoiner.toString();
    }
}
