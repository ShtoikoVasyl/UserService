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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final ModelMapper modelMapper;
    final UserRepository userRepository;
    final RoleRepository roleRepository;

    public UserDto getUserDtoById(long userId) {
        return modelMapper.map(userRepository.findById(userId), UserDto.class);
    }

    @Override
    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public UserDto create(CreateRequestUserDto userRequest) {
        User user = modelMapper.map(userRequest, User.class);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setRole(new Role(1L, "User"));
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new EntityNotFoundException("User with Id=" + userDto.getId() + " not found"));
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    @Transactional
    public User update(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with Id=" + userId + " not found"));
        user.setUserStatus(UserStatus.ARCHIVED);
        return userRepository.save(user);
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
