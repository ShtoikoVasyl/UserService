package edu.shtoiko.userservice.service.implementation;

import edu.shtoiko.userservice.model.Dto.CreateRequestUserDto;
import edu.shtoiko.userservice.model.Dto.UserDto;
import edu.shtoiko.userservice.model.Dto.UserResponse;
import edu.shtoiko.userservice.model.entity.Role;
import edu.shtoiko.userservice.model.entity.User;
import edu.shtoiko.userservice.model.enums.UserStatus;
import edu.shtoiko.userservice.repository.RoleRepository;
import edu.shtoiko.userservice.repository.UserRepository;
import edu.shtoiko.userservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final ModelMapper modelMapper;
    final UserRepository userRepository;
    final RoleRepository roleRepository;

    public UserDto getUserDtoById(long userId) {
        return modelMapper.map(readById(userId), UserDto.class);
    }

    @Override
    public User readById(long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public UserDto create(CreateRequestUserDto userRequest) {
        User user = modelMapper.map(userRequest, User.class);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setRole(new Role(1L, "User"));
        user = userRepository.save(user);
        log.info("New user created, id={}", user.getId());
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto archiveUser(UserDto userDto) {
        User user = readById(userDto.getId());
        user = userRepository.save(user);
        log.info("User updated, id={}", user.getId());
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public User archiveUser(long userId) {
        User user = readById(userId);
        user.setUserStatus(UserStatus.ARCHIVED);
        user = userRepository.save(user);
        log.info("User archived, id={}", userId);
        return user;
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
        User user = readById(userId);
        return modelMapper.map(user, UserResponse.class);
    }
}
