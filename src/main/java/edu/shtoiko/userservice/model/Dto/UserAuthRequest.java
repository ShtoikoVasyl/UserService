package edu.shtoiko.userservice.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAuthRequest {
    private Long id;

    private String email;

    private String password;
}
