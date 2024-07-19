package edu.shtoiko.userservice.model.Dto;

import edu.shtoiko.userservice.model.entity.User;
import lombok.Data;

import java.util.Objects;

@Data
public class UserDto {

    public UserDto() {
    }

    public UserDto(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

//        this.roleList = roleListEntity;
//        this.roleList = roleListEntity.stream()
//                .map(Role::getName).toList();
    }
//
//    public UserDto(Long id, String firstName, String lastName, String email, List<Role> roleEntityList) {
//        this.id = id;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.roleList = roleEntityList.stream().map(Role::getId).toList();
//    }

//    public UserDto(Long id, String firstName, String lastName, String email, String password, String newPassword, Long role) {
//        this.id = id;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.password = password;
//        this.newPassword = newPassword;
//        this.roleId = role;
//    }

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String newPassword;
    private Long roleId;

    public UserDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.roleId = user.getRole().getId();
        this.email = user.getEmail();
    }

    public boolean isNew() {
        return this.id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id) && Objects.equals(firstName, userDto.firstName) && Objects.equals(lastName, userDto.lastName) && Objects.equals(email, userDto.email) && Objects.equals(password, userDto.password) && Objects.equals(newPassword, userDto.newPassword) && Objects.equals(roleId, userDto.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, password, newPassword, roleId);
    }
}
