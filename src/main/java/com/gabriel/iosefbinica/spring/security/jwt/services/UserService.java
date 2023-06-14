package com.gabriel.iosefbinica.spring.security.jwt.services;

import com.gabriel.iosefbinica.spring.security.jwt.domains.*;
import com.gabriel.iosefbinica.spring.security.jwt.exception.TokenRefreshException;
import com.gabriel.iosefbinica.spring.security.jwt.models.ERole;
import com.gabriel.iosefbinica.spring.security.jwt.models.payload.request.LoginRequest;
import com.gabriel.iosefbinica.spring.security.jwt.models.payload.request.SignupRequest;
import com.gabriel.iosefbinica.spring.security.jwt.models.payload.response.MessageResponse;
import com.gabriel.iosefbinica.spring.security.jwt.models.payload.response.UserInfoResponse;
import com.gabriel.iosefbinica.spring.security.jwt.repository.ProjectRepository;
import com.gabriel.iosefbinica.spring.security.jwt.repository.RoleRepository;
import com.gabriel.iosefbinica.spring.security.jwt.repository.UserProjectRepository;
import com.gabriel.iosefbinica.spring.security.jwt.repository.UserRepository;
import com.gabriel.iosefbinica.spring.security.jwt.security.jwt.JwtUtils;
import com.gabriel.iosefbinica.spring.security.jwt.security.services.RefreshTokenService;
import com.gabriel.iosefbinica.spring.security.jwt.security.services.UserDetailsImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;

    public UserService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils,
                       RefreshTokenService refreshTokenService, ProjectRepository projectRepository, UserProjectRepository userProjectRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.projectRepository = projectRepository;
        this.userProjectRepository = userProjectRepository;
    }

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        UserInfoResponse userInfoResponse = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(),
                userDetails.getEmail(), roles);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(userInfoResponse);
    }

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public ResponseEntity<?> logoutUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (!Objects.equals(principal.toString(), "anonymousUser")) {
                Long userId = ((UserDetailsImpl) principal).getId();
                refreshTokenService.deleteByUserId(userId);
            }

            ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
            ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                    .body(new MessageResponse("You have been signed out!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if (refreshToken != null && !refreshToken.isEmpty()) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);

                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new MessageResponse("Token refreshed successfully!"));
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            "Refresh token is not in the database!"));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
    }


    public ResponseEntity<?> getProjectsByUserId(Long id) {
        List<UserProject> projects = userProjectRepository.findByUserId(id);

        if (projects.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(projects);
    }

    public ResponseEntity<?> addTimesheet(Long userId, Long projectId, String selectedDate,Long hours, LocalDate fromDate, LocalDate toDate, LocalDate weekStartDay, LocalDate weekEndDay) {
        User user = userRepository.findById(userId).orElse(null);
        UserProject project = userProjectRepository.findById(projectId).orElse(null);

        if (user == null || project == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User or project not found");
        }

        Timesheet timesheetEntry = new Timesheet();
        timesheetEntry.setUser(user);
        timesheetEntry.setProject(project);
        timesheetEntry.setSelectedDate(LocalDate.parse(selectedDate));
        timesheetEntry.setHours(hours);
        timesheetEntry.setFromDate(fromDate);
        timesheetEntry.setToDate(toDate);
        timesheetEntry.setWeekStartDay(weekStartDay);
        timesheetEntry.setWeekEndDay(weekEndDay);

        // Set the default status as needed
        timesheetEntry.setStatus("Pending");

        timesheetEntry.save(timesheetEntry);

        return ResponseEntity.status(HttpStatus.CREATED).body("Timesheet entry created successfully");
    }
}
