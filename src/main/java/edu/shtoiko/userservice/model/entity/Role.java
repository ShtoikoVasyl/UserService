package edu.shtoiko.userservice.model.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class Role implements GrantedAuthority {

    private long id;

    private String name;

    public String getAuthority() {
        return name;
    }

    public Role(String name) {
        this.name = name;
    }
}
