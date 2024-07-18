package edu.shtoiko.userservice.controller;

import edu.shtoiko.userservice.model.Dto.CreateRequestUserDto;
import edu.shtoiko.userservice.model.Dto.UserDto;
import edu.shtoiko.userservice.model.Dto.UserTransformer;
import edu.shtoiko.userservice.model.entity.User;
import edu.shtoiko.userservice.model.enums.UserStatus;
import edu.shtoiko.userservice.repository.UserRepository;
import edu.shtoiko.userservice.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/")
public class UserController {

    final UserTransformer userTransformer;

    final UserService userService;

    final UserRepository userRepository;

    public UserController(UserTransformer userTransformer, UserService userServiceImp, UserRepository userRepository) {
        this.userTransformer = userTransformer;
        this.userService = userServiceImp;
        this.userRepository = userRepository;
    }



    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateRequestUserDto newUser){
        HttpHeaders header = new HttpHeaders();
        if(newUser == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

//        userService.create(userTransformer.transformToEntity(newUser));
        return new ResponseEntity<>(new UserDto(userService.create(userTransformer.transformToEntity(newUser))), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/")
    public UserDto read(@PathVariable("id") long userId){
//        var user = userService.readById(userId);
//        System.out.println(user);
        return userService.getUserDtoById(userId);
//        return new UserResponse(user);
    }

    @PutMapping("/{id}/")
    public UserDto updateUserById(@PathVariable long id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        User user = userTransformer.convertToUpdatedEntity(userDto);
        return new UserDto(userService.update(user));
    }

//    public UserDto setNewRoleForUserById(@PathVariable long id, @RequestBody UserDto userDto) {
//    }
//    public remuveRole9


    @DeleteMapping("/{id}/")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        try {
            User user = userService.readById(id);
            user.setUserStatus(UserStatus.ARCHIVED);
            userService.update(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
        return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }
    }
}
