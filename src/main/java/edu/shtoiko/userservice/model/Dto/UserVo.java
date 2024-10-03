package edu.shtoiko.userservice.model.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UserVo {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<AccountVo> accounts;
}
