package edu.shtoiko.userservice.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class DefaultFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String jwtToken = "Bearer " + getAuthorizedToken();
        requestTemplate.header("Authorization", jwtToken);
    }

    private String getAuthorizedToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
            Jwt jwt = (Jwt) jwtAuthToken.getCredentials();
            return jwt.getTokenValue();
        } else {
            throw new SecurityException("current Authentication is not instance of JwtAuthenticationToken");
        }
    }
}
