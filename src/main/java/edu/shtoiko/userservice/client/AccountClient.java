package edu.shtoiko.userservice.client;

import edu.shtoiko.userservice.config.AuthClientFeignConfig;
import edu.shtoiko.userservice.config.DefaultFeignConfig;
import edu.shtoiko.userservice.model.Dto.AccountVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ACCOUNTSERVICE", configuration = DefaultFeignConfig.class)
public interface AccountClient {

    @GetMapping("/account/user/{id}/")
    List<AccountVo> getAccountsByUserId(@PathVariable Long id);
}
