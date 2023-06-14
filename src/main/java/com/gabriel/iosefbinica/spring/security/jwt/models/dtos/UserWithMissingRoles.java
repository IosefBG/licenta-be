package com.gabriel.iosefbinica.spring.security.jwt.models.dtos;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Role;
import com.gabriel.iosefbinica.spring.security.jwt.domains.User;

import java.util.List;

public class UserWithMissingRoles {
    private Long id;
    private String username;
    private String email;
    private List<Role> missingRoles;

    public UserWithMissingRoles(User user, List<Role> missingRoles) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.missingRoles = missingRoles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getMissingRoles() {
        return missingRoles;
    }

    public void setMissingRoles(List<Role> missingRoles) {
        this.missingRoles = missingRoles;
    }

}
