package edu.shtoiko.userservice.client;

import edu.shtoiko.userservice.config.AuthClientFeignConfig;
import edu.shtoiko.userservice.interceptor.AuthClientInterceptor;
import edu.shtoiko.userservice.model.Dto.UserAuthRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AUTHSERVICE", configuration = AuthClientFeignConfig.class)
public interface AuthClient {

    @PostMapping("/auth/user/register")
    String registerNewUser(@RequestBody UserAuthRequest userAuthRequest);
}
