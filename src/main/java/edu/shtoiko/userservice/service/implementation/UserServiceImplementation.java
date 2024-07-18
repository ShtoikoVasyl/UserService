package edu.shtoiko.userservice.service.implementation;

import edu.shtoiko.userservice.model.Dto.UserDto;
import edu.shtoiko.userservice.model.entity.Role;
import edu.shtoiko.userservice.model.entity.User;
import edu.shtoiko.userservice.model.enums.UserStatus;
import edu.shtoiko.userservice.repository.RoleRepository;
import edu.shtoiko.userservice.repository.UserRepository;
import edu.shtoiko.userservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService {
    final UserRepository userRepository;

    final RoleRepository roleRepository;

    public UserServiceImplementation(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }



    public UserDto getUserDtoById(long userId) {
        return userRepository.findUserDtoByUserId(userId);
    }

    @Override
    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public User create(User user) {
        user.setUserStatus(UserStatus.ACTIVE);
        user.setRole(new Role(1L, "User"));
        return userRepository.save(user);
    }

//    @Override
//    public User update(User user) {
//        userRepository.findById(user.getId());
//        return userRepository.save(user);
//    }

    @Override
    @Transactional
    public User update(User user) {
        userRepository.findById(user.getId());
        return userRepository.save(user);
    }

    @Override
    public void delete(long id) {
//        userRepository.delete(readById(id));
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
