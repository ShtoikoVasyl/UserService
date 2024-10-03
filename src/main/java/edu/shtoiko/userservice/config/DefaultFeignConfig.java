package edu.shtoiko.userservice.config;

import edu.shtoiko.userservice.interceptor.DefaultFeignInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class DefaultFeignConfig {

    @Bean
    public RequestInterceptor defaultFeinInterceptor() {
        return new DefaultFeignInterceptor();
    }
}
