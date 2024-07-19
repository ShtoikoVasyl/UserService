package edu.shtoiko.userservice.controller;

import edu.shtoiko.userservice.model.Dto.CreateRequestUserDto;
import edu.shtoiko.userservice.model.Dto.UserDto;
import edu.shtoiko.userservice.model.Dto.UserResponse;
import edu.shtoiko.userservice.model.entity.User;
import edu.shtoiko.userservice.model.enums.UserStatus;
import edu.shtoiko.userservice.repository.UserRepository;
import edu.shtoiko.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/")
public class UserController {

    final UserService userService;

    final UserRepository userRepository;

    public UserController(UserService userServiceImp, UserRepository userRepository) {
        this.userService = userServiceImp;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateRequestUserDto newUser){
        if(newUser == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userService.create(newUser), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/")
    public UserDto read(@PathVariable("id") long userId){
        return userService.getUserDtoById(userId);
    }

    @GetMapping("/user/{id}/")
    public UserResponse getUserVoById(@PathVariable("id") long userId){
        return userService.getUserResponseById(userId);
    }

    @PutMapping("/{id}/")
    public UserDto updateUserById(@PathVariable long id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        return userService.update(userDto);
    }

    @DeleteMapping("/{id}/")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        try {
            User user = userService.readById(id);
            user.setUserStatus(UserStatus.ARCHIVED);
            userService.update(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
        return new ResponseEntity<>(id, HttpStatus.NOT_FOUND);
        }
    }
}
