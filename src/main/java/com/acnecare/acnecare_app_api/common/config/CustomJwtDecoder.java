package com.acnecare.acnecare_app_api.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.acnecare.acnecare_app_api.identity.repository.InvalidatedRepository;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    private final InvalidatedRepository invalidatedRepository;
    private final String signerKey;
    private volatile NimbusJwtDecoder nimbusJwtDecoder;

    public CustomJwtDecoder(
            @Value("${jwt.signer-key}") String signerKey,
            InvalidatedRepository invalidatedRepository) {
        this.signerKey = signerKey;
        this.invalidatedRepository = invalidatedRepository;
    }

    private NimbusJwtDecoder jwtDecoder() {
        if (Objects.isNull(nimbusJwtDecoder)) {
            synchronized (this) {
                if (Objects.isNull(nimbusJwtDecoder)) {
                    SecretKeySpec secretKeySpec =
                            new SecretKeySpec(signerKey.getBytes(StandardCharsets.UTF_8), "HS512");
                    nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                            .macAlgorithm(MacAlgorithm.HS512)
                            .build();
                }
            }
        }
        return nimbusJwtDecoder;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        if (token == null || token.isBlank()) {
            throw new JwtException("Token is missing");
        }
        try {
            Jwt jwt = jwtDecoder().decode(token);
            if (!"access".equals(jwt.getClaimAsString("type"))) {
                throw new JwtException("Invalid token type");
            }
            String jti = jwt.getId();
            if (jti != null && invalidatedRepository.existsById(jti)) {
                throw new JwtException("Token has been revoked");
            }
            return jwt;
        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtException(e.getMessage(), e);
        }
    }
}
