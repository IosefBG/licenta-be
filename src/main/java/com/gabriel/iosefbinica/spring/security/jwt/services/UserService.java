package com.gabriel.iosefbinica.spring.security.jwt.services;

import com.gabriel.iosefbinica.spring.security.jwt.domains.*;
import com.gabriel.iosefbinica.spring.security.jwt.exception.TokenRefreshException;
import com.gabriel.iosefbinica.spring.security.jwt.models.ERole;
import com.gabriel.iosefbinica.spring.security.jwt.models.payload.request.LoginRequest;
import com.gabriel.iosefbinica.spring.security.jwt.models.payload.request.SignupRequest;
import com.gabriel.iosefbinica.spring.security.jwt.models.payload.response.MessageResponse;
import com.gabriel.iosefbinica.spring.security.jwt.models.payload.response.UserInfoResponse;
import com.gabriel.iosefbinica.spring.security.jwt.repository.*;
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
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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
    private final TimesheetRepository timesheetRepository;

    public UserService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils,
                       RefreshTokenService refreshTokenService, ProjectRepository projectRepository, UserProjectRepository userProjectRepository, TimesheetRepository timesheetRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.projectRepository = projectRepository;
        this.userProjectRepository = userProjectRepository;
        this.timesheetRepository = timesheetRepository;
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

    @Transactional
    public ResponseEntity<?> addTimesheet(Long userId, Long projectId, String selectedDate, Long hours, LocalDate fromDate, LocalDate toDate) {
        User user = userRepository.findById(userId).orElse(null);
        UserProject project = userProjectRepository.findByProjectIdAndUserId(projectId, userId).orElse(null);

        if (user == null || project == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User or project not found");
        }

        // Check if the selectedDate is within the specified week
        if ((fromDate != null && selectedDate != null && fromDate.isAfter(LocalDate.parse(selectedDate))) ||
                (toDate != null && selectedDate != null && toDate.isBefore(LocalDate.parse(selectedDate)))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Selected date is outside the specified week");
        }

        // Handle the case when a single day is selected
        if (selectedDate != null) {
            String datePart = selectedDate.split("T")[0];

            // Check if an existing timesheet entry exists for the same project, user, and date
            Timesheet existingEntry = timesheetRepository.findByUserAndProjectAndSelectedDate(user, project, LocalDate.parse(datePart)).orElse(null);
            if (existingEntry != null && existingEntry.getProjectId() == project.getUserProjectId()) {
                // Update the existing entry
                existingEntry.setHours(hours);
                existingEntry.setFromDate(LocalDate.parse(datePart));
                existingEntry.setToDate(LocalDate.parse(datePart));

                timesheetRepository.save(existingEntry);

                return ResponseEntity.status(HttpStatus.OK).body(existingEntry);
            } else {
                // Create a new entry
                Timesheet timesheetEntry = new Timesheet();
                timesheetEntry.setUser(user);
                timesheetEntry.setProject(project);
                timesheetEntry.setSelectedDate(LocalDate.parse(datePart));
                timesheetEntry.setHours(hours);
                timesheetEntry.setFromDate(LocalDate.parse(datePart));
                timesheetEntry.setToDate(LocalDate.parse(datePart));

                // Set the default status as needed
                timesheetEntry.setStatus("Initializat");

                timesheetRepository.save(timesheetEntry);

                return ResponseEntity.status(HttpStatus.CREATED).body(timesheetEntry);
            }
        }

        // Handle the case when a period is selected
        if (fromDate != null && toDate != null) {
            // Validate that the fromDate and toDate fall within the same week
            LocalDate startOfWeekFrom = fromDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate startOfWeekTo = toDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            if (!startOfWeekFrom.equals(startOfWeekTo)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid period. The fromDate and toDate must be within the same week");
            }

            // Remove the existing timesheet entries within the period if they exist
            timesheetRepository.deleteByUserAndProjectAndSelectedDateBetween(user, project, fromDate, toDate);

            // Iterate over the dates in the selected period and create timesheet entries for each date
            LocalDate currentDate = fromDate;
            List<Timesheet> timesheetEntries = new ArrayList<>();
            while (!currentDate.isAfter(toDate)) {
                Timesheet timesheetEntry = new Timesheet();
                timesheetEntry.setUser(user);
                timesheetEntry.setProject(project);
                timesheetEntry.setSelectedDate(currentDate);
                timesheetEntry.setHours(hours);
                timesheetEntry.setFromDate(fromDate);
                timesheetEntry.setToDate(toDate);

                // Set the default status as needed
                timesheetEntry.setStatus("Init");

                timesheetRepository.save(timesheetEntry);
                timesheetEntries.add(timesheetEntry);

                currentDate = currentDate.plusDays(1);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(timesheetEntries);
        }

        // If neither a day nor a period is selected, return a bad request response
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid timesheet selection");
    }


    public ResponseEntity<?> getTimesheetByUserId(Long userId, String weekStartDate) {
        // Calculate the end date of the week by adding 6 days to the start date
        LocalDate parsedWeekStartDate = LocalDate.parse(weekStartDate);
        LocalDate weekEndDate = parsedWeekStartDate.plusDays(6);

        List<Timesheet> timesheets = timesheetRepository.findByUserIdAndDateRange(userId, parsedWeekStartDate, weekEndDate);

        if (timesheets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(timesheets);
    }

    public ResponseEntity<?> deleteTimesheetEntry(Long timesheetId) {
        timesheetRepository.deleteById(timesheetId);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> updateTimesheetEntry(Long userId, String status, String startWeek, String endWeek) {
        LocalDate startDate = LocalDate.parse(startWeek);
        LocalDate endDate = LocalDate.parse(endWeek);

        // Fetch timesheet entries based on the specified conditions
        List<Timesheet> timesheetEntries = timesheetRepository.findByUserIdAndToDateBetween(userId, startDate, endDate);

        // Update the status for each timesheet entry
        for (Timesheet entry : timesheetEntries) {
            entry.setStatus(status);
        }

        // Save the updated timesheet entries
        timesheetRepository.saveAll(timesheetEntries);

        return ResponseEntity.ok("Timesheet entries updated successfully.");
    }
}
