package edu.shtoiko.userservice.service;

import edu.shtoiko.userservice.model.Dto.*;
import edu.shtoiko.userservice.model.entity.User;

import java.util.List;

public interface UserService {
    public UserVo getUserDtoById(long userId);

    User readUserById(long id);

    UserResponse create(CreateRequestUserDto user);

    UserVo archiveUser(Long userId);

    void delete(long id);

    List<User> getAll();

    UserResponse getUserResponseById(long userId);

    UserResponse updateUser(UserUpdateRequest userDto);

    UserResponse getUserResponseByEmail(String email);

    UserVo getUserVoById(Long id);
}
