package edu.shtoiko.userservice.service;

import edu.shtoiko.userservice.model.Dto.UserDto;
import edu.shtoiko.userservice.model.entity.User;

import java.util.List;


public interface UserService {
//    User readByName(String name);

    public UserDto getUserDtoById(long userId);
    User readById(long id);
    User create(User user);
    User update(User user);
    void delete(long id);
    List<User> getAll();
}
