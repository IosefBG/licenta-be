package com.gabriel.iosefbinica.spring.security.jwt.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/manage")
    @PreAuthorize("hasRole('MANAGER')")
    public String moderatorAccess() {
        return "Manager Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
//    @GetMapping("/admin")
//    public String adminAccess() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        String username = userDetails.getUsername();
//        return "Admin Board for " + username + ".";
//}
}

