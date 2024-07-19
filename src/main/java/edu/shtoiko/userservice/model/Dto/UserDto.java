package edu.shtoiko.userservice.model.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String newPassword;
    private Long roleId;

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
