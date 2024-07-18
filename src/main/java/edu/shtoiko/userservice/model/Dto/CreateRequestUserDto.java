package edu.shtoiko.userservice.model.Dto;

import lombok.Data;

@Data
public class CreateRequestUserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
