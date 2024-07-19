package edu.shtoiko.userservice.model.mapper;

import edu.shtoiko.userservice.model.Dto.AccountVo;
import edu.shtoiko.userservice.model.Dto.UserResponse;
import edu.shtoiko.userservice.model.entity.User;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class UserToResponseConverter implements Converter<User, UserResponse> {

    private final RestTemplate restTemplate;

    @Value("${accountService.address}")
    private String address;

    public UserToResponseConverter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserResponse convert(MappingContext<User, UserResponse> context) {
        User user = context.getSource();
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());

        try {
            List<AccountVo> accounts = List.of(restTemplate.getForObject(address + userResponse.getId() + "/", AccountVo[].class));
            userResponse.setAccounts(accounts);
        } catch (RestClientException e) {
            // Логування помилки або інша обробка
            System.err.println("Failed to retrieve accounts: " + e.getMessage());
            // Установити порожній список або інше значення за замовчуванням
            userResponse.setAccounts(Collections.emptyList());
        }

        return userResponse;
    }
}