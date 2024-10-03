package edu.shtoiko.userservice.interceptor;

import edu.shtoiko.userservice.model.entity.Role;
import edu.shtoiko.userservice.utils.JwtTokenUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@RequiredArgsConstructor
public class AuthClientInterceptor implements RequestInterceptor {

    private final JwtTokenUtils tokenUtils;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String jwtToken = "Bearer " + getAuthorizedToken();
        requestTemplate.header("Authorization", jwtToken);
    }

    private String getAuthorizedToken() {
        return tokenUtils.generateCrossServiceAccessToken(configureServiceUser());
    }

    private User configureServiceUser() {
        return new User("USER_SERVICE", "", List.of(new Role("USERMANAGER_WRITE")));
    }
}
