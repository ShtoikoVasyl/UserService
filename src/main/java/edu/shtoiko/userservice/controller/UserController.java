package edu.shtoiko.userservice.controller;

import edu.shtoiko.userservice.model.Dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(summary = "Create a new user",
        description = "Creates a new user with the provided details.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "409", description = "User with this email already exist"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<?> create(
        @Parameter(description = "New user details",
            required = true) @Valid @RequestBody CreateRequestUserDto newUser) {
        return new ResponseEntity<>(userService.create(newUser), HttpStatus.CREATED);
    }

    @Operation(summary = "Get user details by ID",
        description = "Retrieves the details of a user by their ID. " +
            "Accessible to the user themselves, or users USERMANAGER_READ role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("#userId == authentication.details or hasAuthority('USERMANAGER_READ')")
    public ResponseEntity<?> read(
        @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable("id") String userId) {
        return new ResponseEntity<>(userService.getUserResponseById(Long.parseLong(userId)), HttpStatus.OK);
    }

    @Operation(summary = "Get user details by email",
        description = "Retrieves the details of a user by their email. " +
            "Accessible only to users with USERMANAGER_READ role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping()
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('USERMANAGER_READ')")
    public ResponseEntity<?> readByEmail(
        @Parameter(description = "Email of the user to retrieve", required = true) @RequestParam String email) {
        return new ResponseEntity<>(userService.getUserResponseByEmail(email), HttpStatus.OK);
    }

    @Operation(summary = "Get user and account details by ID",
        description = "Retrieves the response object of a user and their accounts by their ID. " +
            "Accessible to the user themselves, or users with USERMANAGER_READ role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User and account details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserVo.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{userId}/")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("#userId == authentication.details or hasAuthority('USERMANAGER_READ')")
    public ResponseEntity<?> getUserVoById(
        @Parameter(description = "ID of the user to retrieve the response for",
            required = true) @PathVariable String userId) {
        return new ResponseEntity<>(userService.getUserVoById(Long.parseLong(userId)), HttpStatus.OK);
    }

    @Operation(summary = "Update user details",
        description = "Updates the details of an existing user. " +
            "Accessible to the user themselves, or users with USERMANAGER_WRITE role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User details updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping()
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("#user.id == authentication.details or hasAuthority('USERMANAGER_WRITE')")
    public ResponseEntity<?> updateUser(
        @Parameter(description = "Updated user details", required = true) @Valid @RequestBody UserUpdateRequest user) {
        return new ResponseEntity<>(userService.updateUser(user), HttpStatus.OK);
    }

    @Operation(summary = "Archive user by ID",
        description = "Archives a user by their ID. " +
            "Accessible to the user themselves, or users with USERMANAGER_WRITE role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User archived successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{userId}/")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("#userId == authentication.details or hasAuthority('USERMANAGER_WRITE')")
    public ResponseEntity<Long> delete(
        @Parameter(description = "ID of the user to archive", required = true) @PathVariable String userId) {
        userService.archiveUser(Long.parseLong(userId));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}