package com.acnecare.acnecare_app_api.identity.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import com.acnecare.acnecare_app_api.identity.service.AuthenticationService;
import com.acnecare.acnecare_app_api.common.dto.ApiResponse;
import com.acnecare.acnecare_app_api.identity.dto.request.AuthenticationRequest;
import com.acnecare.acnecare_app_api.identity.dto.response.AuthenticationResponse;
import java.text.ParseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.nimbusds.jose.JOSEException;
import com.acnecare.acnecare_app_api.identity.dto.response.IntrospectResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import com.acnecare.acnecare_app_api.common.exception.AppException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request,
            HttpServletResponse httpResponse,
            HttpServletRequest httpRequest
    ) throws JOSEException, ParseException, AppException {
        return ApiResponse.<AuthenticationResponse>builder()
            .code(1000)
            .message("Login successful")
            .result(authenticationService.login(request, httpResponse))
            .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(
            HttpServletResponse httpResponse,
            HttpServletRequest httpRequest
            ) 
    throws JOSEException, ParseException, AppException {
        authenticationService.logout(httpRequest, httpResponse);
        return ApiResponse.<Void>builder()
            .code(1000)
            .message("Logout successful")
            .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(
            HttpServletRequest httpRequest
            ) 
    throws JOSEException, ParseException {
        return ApiResponse.<IntrospectResponse>builder()
            .code(1000)
            .message("Introspect successful")
            .result(authenticationService.introspect(httpRequest))
            .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refresh(
            HttpServletResponse httpResponse,
            HttpServletRequest httpRequest
            ) 
    throws JOSEException, ParseException, AppException {
        return ApiResponse.<AuthenticationResponse>builder()
            .code(1000)
            .message("Refresh successful")
            .result(authenticationService.refreshToken(httpResponse, httpRequest))
            .build();
    } 

}
