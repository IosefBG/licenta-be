package com.gabriel.iosefbinica.spring.security.jwt.controllers;

import com.gabriel.iosefbinica.spring.security.jwt.domains.User;
import com.gabriel.iosefbinica.spring.security.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
public class ManagerController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/users") // GET: http://localhost:8080/api/manager/users
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}