package edu.shtoiko.userservice.service;

import edu.shtoiko.userservice.UserTestDataFactory;
import edu.shtoiko.userservice.client.AuthClient;
import edu.shtoiko.userservice.exception.ResponseException;
import edu.shtoiko.userservice.model.Dto.*;
import edu.shtoiko.userservice.model.entity.User;
import edu.shtoiko.userservice.model.enums.UserStatus;
import edu.shtoiko.userservice.repository.UserRepository;
import edu.shtoiko.userservice.service.implementation.UserServiceImplementation;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthClient authClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final PasswordEncoder originalPasswordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UserServiceImplementation userService;

    @Test
    void getUserDtoById_ExistingUser_ReturnsUser() {
        User mockUser = UserTestDataFactory.getValidActiveUserJohn();
        UserVo mockUserVo = UserTestDataFactory.getValidActiveUserVoJohn();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(modelMapper.map(mockUser, UserVo.class)).thenReturn(mockUserVo);

        UserVo result = userService.getUserDtoById(1L);

        assertNotNull(result);
        assertEquals(mockUserVo, result);
    }

    @Test
    void getUserDtoById_NonExistingUser_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseException.class, () -> userService.getUserDtoById(1L));
    }

    @Test
    void readUserById_ExistingUser_ReturnsUser() {
        User mockUser = UserTestDataFactory.getValidActiveUserJohn();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User result = userService.readUserById(1L);

        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    void readUserById_NonExistingUser_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseException.class, () -> userService.readUserById(1L));
    }

    @Test
    void saveUser_UserSaved_ReturnUserResponse() {
        User user = new User(1L, "John", "Doe", null, "john@example.com");
        User mockUser = UserTestDataFactory.getValidActiveUserJohn();
        UserResponse expected = UserTestDataFactory.getValidActiveUserResponseJohn();
        System.out.println("password: " + originalPasswordEncoder.encode("123@Qwerty"));
        CreateRequestUserDto createRequestUserDto = new CreateRequestUserDto("John", "Doe", "john@example.com", "123@Qwerty");
        UserAuthRequest authRequest = new UserAuthRequest(1L, "john@example.com", "$2a$10$ED3jYQWhBQpzQv6mQQL9J.Y8ar9sA3MTc5Lr2a3l/JMKgeGbSJj72");

        when(passwordEncoder.encode("123@Qwerty")).thenReturn("$2a$10$ED3jYQWhBQpzQv6mQQL9J.Y8ar9sA3MTc5Lr2a3l/JMKgeGbSJj72");
        when(modelMapper.map(createRequestUserDto, User.class)).thenReturn(user);
        when(modelMapper.map(UserTestDataFactory.getValidActiveUserJohn(), UserResponse.class)).thenReturn(UserTestDataFactory.getValidActiveUserResponseJohn());
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(authClient.registerNewUser(authRequest)).thenReturn("AuthService response");

        UserResponse result = userService.saveUser(createRequestUserDto);

        assertEquals(expected, result);
        assertTrue(originalPasswordEncoder.matches("123@Qwerty", authRequest.getPassword()));
    }

    @Test
    void saveUser_UserSavingInAuthServiceFailed_ThrowsException() {
        User user = new User(1L, "John", "Doe", null, "john@example.com");
        User mockUser = UserTestDataFactory.getValidActiveUserJohn();
        CreateRequestUserDto createRequestUserDto = new CreateRequestUserDto("John", "Doe", "john@example.com", "123@Qwerty");
        UserAuthRequest authRequest = new UserAuthRequest(1L, "john@example.com", "$2a$10$ED3jYQWhBQpzQv6mQQL9J.Y8ar9sA3MTc5Lr2a3l/JMKgeGbSJj72");
        FeignException feignException = FeignException.errorStatus(
                "registerNewUser",
                feign.Response.builder()
                        .request(Request.create(Request.HttpMethod.POST, "/users", new HashMap<>(), null, new RequestTemplate()))
                        .status(400)
                        .reason("Invalid request data")
                        .build()
        );

        when(passwordEncoder.encode("123@Qwerty")).thenReturn("$2a$10$ED3jYQWhBQpzQv6mQQL9J.Y8ar9sA3MTc5Lr2a3l/JMKgeGbSJj72");
        when(modelMapper.map(createRequestUserDto, User.class)).thenReturn(user);
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(authClient.registerNewUser(authRequest)).thenThrow(feignException);

        assertThrows(ResponseException.class, () -> userService.saveUser(createRequestUserDto));
    }

    @Test
    void saveUser_CreateRequestIsNull_ThrowsException() {
        assertThrows(NullPointerException.class, () -> userService.saveUser(null));
    }

    @Test
    void archiveUser_UserArchived_ReturnsUserVo() {
        Long userId = 1L;
        User user = new User(userId, "John", "Doe", UserStatus.ACTIVE, "john@example.com");
        User archivedUser = new User(userId, "John", "Doe", UserStatus.ARCHIVED, "john@example.com");
        UserVo expectedUserVo = new UserVo(userId, "John", "Doe", "john@example.com", List.of(UserTestDataFactory.getValidAccountVo()));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(archivedUser);
        when(modelMapper.map(archivedUser, UserVo.class)).thenReturn(expectedUserVo);

        UserVo result = userService.archiveUser(userId);

        assertNotNull(result);
        assertEquals(expectedUserVo, result);
        verify(userRepository).save(user);
    }

    @Test
    void delete_DeletesUserById() {
        Long userId = 1L;

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void getAll_ReturnsListOfUsers() {
        List<User> users = List.of(
                new User(1L, "John", "Doe", UserStatus.ACTIVE, "john@example.com"),
                new User(2L, "Alice", "Smith", UserStatus.ACTIVE, "alice@example.com")
        );

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(users));
    }

    @Test
    void getUserResponseById_ReturnsUserResponse() {
        Long userId = 1L;
        User user = new User(userId, "John", "Doe", UserStatus.ACTIVE, "john@example.com");
        UserResponse expectedResponse = new UserResponse(userId, "John", "Doe", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponse.class)).thenReturn(expectedResponse);

        UserResponse result = userService.getUserResponseById(userId);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void getUserVoById_ReturnsUserVo() {
        Long userId = 1L;
        User user = new User(userId, "John", "Doe", UserStatus.ACTIVE, "john@example.com");
        UserVo expectedVo = new UserVo(userId, "John", "Doe", "john@example.com", List.of(UserTestDataFactory.getValidAccountVo()));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserVo.class)).thenReturn(expectedVo);

        UserVo result = userService.getUserVoById(userId);

        assertNotNull(result);
        assertEquals(expectedVo, result);
    }

    @Test
    void updateUser_UserUpdated_ReturnsUserResponse() {
        UserUpdateRequest updateRequest = new UserUpdateRequest("1", "new.email@example.com", "NewFirstName", "NewLastName");
        User existingUser = new User(1L, "John", "Doe", UserStatus.ACTIVE, "john@example.com");

        User updatedUser = new User(1L, "NewFirstName", "NewLastName", UserStatus.ACTIVE, "new.email@example.com");
        UserResponse expectedResponse = new UserResponse(1L, "NewFirstName", "NewLastName", "new.email@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserResponse.class)).thenReturn(expectedResponse);

        UserResponse result = userService.updateUser(updateRequest);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void getUserResponseByEmail_ReturnsUserResponse() {
        String email = "john@example.com";
        User user = new User(1L, "John", "Doe", UserStatus.ACTIVE, email);
        UserResponse expectedResponse = new UserResponse(1L, "John", "Doe", email);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(modelMapper.map(user, UserResponse.class)).thenReturn(expectedResponse);

        UserResponse result = userService.getUserResponseByEmail(email);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    void readUserByEmail_UserNotFound_ThrowsException() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            userService.getUserResponseByEmail(email);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("User with email notfound@example.com not found"));
    }
}
