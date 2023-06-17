package com.gabriel.iosefbinica.spring.security.jwt.controllers;

import com.gabriel.iosefbinica.spring.security.jwt.models.payload.request.LoginRequest;
import com.gabriel.iosefbinica.spring.security.jwt.models.payload.request.SignupRequest;
import com.gabriel.iosefbinica.spring.security.jwt.models.payload.request.TimesheetRequest;
import com.gabriel.iosefbinica.spring.security.jwt.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.authenticateUser(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return userService.registerUser(signUpRequest);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        return userService.logoutUser();
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        return userService.refreshToken(request);
    }

    @GetMapping("/getProjectsByUserId")
    public ResponseEntity<?> getProjectsByUserId(@RequestParam Long userId) {
        return userService.getProjectsByUserId(userId);
    }

    @PutMapping("/addTimesheet")
    public ResponseEntity<?> addTimesheet(@RequestBody TimesheetRequest request) {
        try {
            return userService.addTimesheet(request.getUserId(), request.getProjectId(), request.getSelectedDate(),
                    request.getHours(), request.getFromDate(), request.getToDate());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

}
