package edu.shtoiko.userservice.controller;

import edu.shtoiko.userservice.model.Dto.CreateRequestUserDto;
import edu.shtoiko.userservice.model.Dto.UserDto;
import edu.shtoiko.userservice.model.Dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import edu.shtoiko.userservice.repository.UserRepository;
import edu.shtoiko.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/")
@Tag(name = "User Controller", description = "Controller for managing users")
public class UserController {

    final UserService userService;

    final UserRepository userRepository;

    public UserController(UserService userServiceImp, UserRepository userRepository) {
        this.userService = userServiceImp;
        this.userRepository = userRepository;
    }
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<UserDto> create(
            @Parameter(description = "New user details", required = true)
            @Valid @RequestBody CreateRequestUserDto newUser) {
        if (newUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userService.create(newUser), HttpStatus.CREATED);
    }

    @Operation(summary = "Get user details by ID", description = "Retrieves the details of a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/")
    public UserDto read(
            @Parameter(description = "ID of the user to retrieve", required = true)
            @PathVariable("id") long userId) {
        return userService.getUserDtoById(userId);
    }

    @Operation(summary = "Get user response by ID", description = "Retrieves the response object of a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User response retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{id}/")
    public UserResponse getUserVoById(
            @Parameter(description = "ID of the user to retrieve the response for", required = true)
            @PathVariable("id") long userId) {
        return userService.getUserResponseById(userId);
    }

    @Operation(summary = "Update user details", description = "Updates the details of an existing user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping
    public UserDto updateUserById(
            @Parameter(description = "Updated user details", required = true)
            @Valid @RequestBody UserDto userDto) {
        return userService.archiveUser(userDto);
    }

    @Operation(summary = "Delete user by ID", description = "Archives a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User archived successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}/")
    public ResponseEntity<Long> delete(
            @Parameter(description = "ID of the user to archive", required = true)
            @PathVariable Long id) {
        userService.archiveUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}