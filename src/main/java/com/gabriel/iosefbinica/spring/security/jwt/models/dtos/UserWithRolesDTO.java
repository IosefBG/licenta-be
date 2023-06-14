package com.gabriel.iosefbinica.spring.security.jwt.models.dtos;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Role;
import com.gabriel.iosefbinica.spring.security.jwt.domains.User;
import java.util.Set;

public class UserWithRolesDTO {
    private final Long id;
    private final String username;
    private final Set<Role> roles;

    public UserWithRolesDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.roles = user.getRoles();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}
