package edu.shtoiko.userservice.service.implementation;

import edu.shtoiko.userservice.client.AuthClient;
import edu.shtoiko.userservice.exception.ResponseException;
import edu.shtoiko.userservice.model.Dto.*;
import edu.shtoiko.userservice.model.entity.User;
import edu.shtoiko.userservice.model.enums.UserStatus;
import edu.shtoiko.userservice.repository.UserRepository;
import edu.shtoiko.userservice.service.UserService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    private final AuthClient authClient;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserVo getUserDtoById(long userId) {
        return modelMapper.map(readUserById(userId), UserVo.class);
    }

    @Override
    public User readUserById(long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResponseException(HttpStatus.BAD_REQUEST, "User with id " + id + " not found"));
    }

    // todo: in case exception than user must be deleted in auth service
    @Override
    @Transactional
    public UserResponse create(CreateRequestUserDto userRequest) {
        User user = modelMapper.map(userRequest, User.class);
        user.setUserStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);
        log.info("New user created, id={}", user.getId());
        try {
            registerNewUserInAuthService(
                new UserAuthRequest(user.getId(), user.getEmail(), passwordEncoder.encode(userRequest.getPassword())));
        } catch (FeignException e) {
            throw new ResponseException(e.status(), e.contentUTF8());
        }
        return modelMapper.map(user, UserResponse.class);
    }

    private String registerNewUserInAuthService(UserAuthRequest userRequest) {
        String result;
        try {
            result = authClient.registerNewUser(userRequest);
            log.info("User id={} : sent register request", userRequest.getId());
        } catch (FeignException e) {
            log.error("Error registering user in Auth Service: {}", e.getMessage());
            throw e;
        }
        return result;
    }

    // todo: archived user's accounts should be blocked
    @Override
    @Transactional
    public UserVo archiveUser(Long userId) {
        User user = readUserById(userId);
        user.setUserStatus(UserStatus.ARCHIVED);
        user = userRepository.save(user);
        log.info("User archived, id={}", userId);
        return modelMapper.map(user, UserVo.class);
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public UserResponse getUserResponseById(long userId) {
        User user = readUserById(userId);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserVo getUserVoById(Long id) {
        User user = readUserById(id);
        return modelMapper.map(user, UserVo.class);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserUpdateRequest updateUser) {
        User user = readUserById(Long.parseLong(updateUser.getId()));
        updateUser(user, updateUser);
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Override
    public UserResponse getUserResponseByEmail(String email) {
        return modelMapper.map(readUserByEmail(email), UserResponse.class);
    }

    private User updateUser(User user, UserUpdateRequest newUser) {
        user.setEmail(newUser.getEmail());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        return user;
    }

    private User readUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, "User with email " + email + " not found");
        }
        return user;
    }
}
