package edu.shtoiko.userservice.config;

import edu.shtoiko.userservice.interceptor.AuthClientInterceptor;
import edu.shtoiko.userservice.utils.JwtTokenUtils;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
@RequiredArgsConstructor
public class AuthClientFeignConfig {

    private final JwtTokenUtils tokenUtils;

    @Bean
    public RequestInterceptor authClientInterceptor() {
        return new AuthClientInterceptor(tokenUtils);
    }
}
