package edu.shtoiko.userservice.model.Dto;

import edu.shtoiko.userservice.model.entity.User;
import edu.shtoiko.userservice.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserTransformer {

    final AccountService accountService;
    final UserService userService;

    public UserTransformer(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

//    public User transformToEntity(UserDto userDto){
//        User newUser = new User();
//        newUser.setId(userDto.getId());
//        newUser.setFirstName(userDto.getFirstName());
//        newUser.setLastName(userDto.getLastName());
//        newUser.setEmail(userDto.getEmail());
////        newUser.setRoles(userDto.getRoleList());
//        return newUser;
//    }

    public User transformToEntity(CreateRequestUserDto userDto){
        User newUser = new User();
        newUser.setFirstName(userDto.getFirstName());
        newUser.setLastName(userDto.getLastName());
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(userDto.getPassword());
        return newUser;
    }

    public User convertToUpdatedEntity(UserDto userDto){
        User updateUser = userService.readById(userDto.getId());
        updateUser.setFirstName(userDto.getFirstName());
        updateUser.setLastName(userDto.getLastName());
        updateUser.setEmail(userDto.getEmail());
        updateUser.setPassword(userDto.getNewPassword());
//        updateUser.setRoles(userDto.getRoleList());
        return updateUser;
    }


//    public UserDto convertToDto(User user) {
//        return new UserDto(
//                user.getId(),
//                user.getFirstName(),
//                user.getLastName(),
//                user.getEmail(),
//                user.getPassword(),
//                null,
//                user.getRoles().stream().map(Role::getId).toList()
//        );
//    }
}
