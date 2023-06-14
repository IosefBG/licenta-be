package com.gabriel.iosefbinica.spring.security.jwt.controllers;

import com.gabriel.iosefbinica.spring.security.jwt.models.payload.request.LoginRequest;
import com.gabriel.iosefbinica.spring.security.jwt.models.payload.request.SignupRequest;
import com.gabriel.iosefbinica.spring.security.jwt.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;

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
    public ResponseEntity<?> addTimesheet(@RequestParam Long userId,
                                          @RequestParam Long projectId,
                                          @RequestParam String selectedDate,
                                          @RequestParam Long hours,
                                          @RequestParam LocalDate fromDate,
                                          @RequestParam LocalDate toDate,
                                          @RequestParam LocalDate weekStartDay,
                                          @RequestParam LocalDate weekEndDay) {
        return userService.addTimesheet(userId, projectId, selectedDate,hours, fromDate, toDate, weekStartDay, weekEndDay);
    }
}
