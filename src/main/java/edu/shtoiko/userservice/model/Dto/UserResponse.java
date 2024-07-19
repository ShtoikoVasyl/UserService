package edu.shtoiko.userservice.model.Dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
public class UserResponse {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<AccountVo> accounts;
}
