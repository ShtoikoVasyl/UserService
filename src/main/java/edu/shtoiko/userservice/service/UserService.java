package edu.shtoiko.userservice.service;

import edu.shtoiko.userservice.model.Dto.CreateRequestUserDto;
import edu.shtoiko.userservice.model.Dto.UserDto;
import edu.shtoiko.userservice.model.Dto.UserResponse;
import edu.shtoiko.userservice.model.entity.User;

import java.util.List;

public interface UserService {
    public UserDto getUserDtoById(long userId);

    User readById(long id);

    UserDto create(CreateRequestUserDto user);

    UserDto archiveUser(UserDto user);

    User archiveUser(long userId);

    void delete(long id);

    List<User> getAll();

    UserResponse getUserResponseById(long userId);
}
