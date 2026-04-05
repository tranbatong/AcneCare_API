package com.acnecare.acnecare_app_api.common.config;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.acnecare.acnecare_app_api.identity.service.AuthCookieService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CookieOrHeaderBearerTokenResolver implements BearerTokenResolver {

    private final AuthCookieService authCookieService;
    private final DefaultBearerTokenResolver headerResolver = new DefaultBearerTokenResolver();

    @Override
    public String resolve(HttpServletRequest request) {
        String fromCookie = authCookieService.readAccessToken(request);
        if (StringUtils.hasText(fromCookie)) {
            return fromCookie.trim();
        }
        return headerResolver.resolve(request);
    }
}
