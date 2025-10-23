package edu.shtoiko.userservice;

import edu.shtoiko.userservice.model.Dto.AccountVo;
import edu.shtoiko.userservice.model.Dto.UserResponse;
import edu.shtoiko.userservice.model.Dto.UserVo;
import edu.shtoiko.userservice.model.entity.User;
import edu.shtoiko.userservice.model.enums.UserStatus;

import java.util.List;

public class UserTestDataFactory {
    public static User getValidActiveUserJohn() {
        return new User(1L, "John", "Doe", UserStatus.ACTIVE , "john@example.com");
    }

    public static UserVo getValidActiveUserVoJohn() {
        return new UserVo(1L, "John", "Doe", "john@example.com", List.of(getValidAccountVo()));
    }

    public static AccountVo getValidAccountVo(){
        return new AccountVo(2L, 1L, "Test John's account", 1234_0000_1234_0000L, "EUR", 100, "CURRENT_ACCOUNT", "OK");
    }

    public static UserResponse getValidActiveUserResponseJohn() {
        return new UserResponse(1L, "John", "Doe", "john@example.com");
    }
}
