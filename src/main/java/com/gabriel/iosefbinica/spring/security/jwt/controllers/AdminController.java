package com.gabriel.iosefbinica.spring.security.jwt.controllers;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Project;
import com.gabriel.iosefbinica.spring.security.jwt.domains.User;
import com.gabriel.iosefbinica.spring.security.jwt.domains.UserProject;
import com.gabriel.iosefbinica.spring.security.jwt.models.dtos.UserWithMissingRoles;
import com.gabriel.iosefbinica.spring.security.jwt.models.dtos.UserWithRolesDTO;
import com.gabriel.iosefbinica.spring.security.jwt.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/usersWithUserRole")
    public ResponseEntity<?> getUsersWithUserRole() {
        List<User> usersWithUserRole = adminService.getUsersWithUserRole();
        return ResponseEntity.ok(usersWithUserRole);
    }

    @PutMapping("/addProject")
    public ResponseEntity<?> addProject(@RequestParam String projectName, @RequestParam Long managerId) {
        adminService.addProject(projectName, managerId);
        return ResponseEntity.ok("Project added");
    }

    @GetMapping("/getManagers")
    public ResponseEntity<?> getManagers() {
        List<User> managers = adminService.getManagers();
        return ResponseEntity.ok(managers);
    }

    @GetMapping("/getManagersWithProjects")
    public ResponseEntity<?> getManagersWithProjects() {
        List<Map<String, Object>> managersWithProjects = adminService.getManagersWithProjects();
        return ResponseEntity.ok(managersWithProjects);
    }

    @GetMapping("/getProjects")
    public ResponseEntity<?> getProjects() {
        List<Project> projects = adminService.getProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/getRoles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok(adminService.getRoles());
    }

    @GetMapping("/getUsersWithMissingRoles")
    public ResponseEntity<?> getAllUsersWithMissingRoles() {
        List<UserWithMissingRoles> usersWithMissingRoles = adminService.getAllUsersWithMissingRoles();
        return ResponseEntity.ok(usersWithMissingRoles);
    }

    @PutMapping("/addRoleForUserId")
    public ResponseEntity<?> addRoleForUserId(@RequestParam Long userId, @RequestParam Integer roleId) {
        adminService.addRoleForUserId(userId, roleId);
        return ResponseEntity.ok("Role added");
    }

    @GetMapping("/getUsersWithRoles")
    public ResponseEntity<List<UserWithRolesDTO>> getUsersWithRoles() {
        List<UserWithRolesDTO> usersWithRoles = adminService.getUsersWithRoles();
        return ResponseEntity.ok(usersWithRoles);
    }

    @DeleteMapping("/removeRoleForUserId")
    public ResponseEntity<?> deleteRoleForUserId(@RequestParam Long userId, @RequestParam Long roleId) {
        adminService.deleteRoleForUserId(userId, roleId);
        return ResponseEntity.ok("Role deleted");
    }

    @PutMapping("/addUserProject")
    public ResponseEntity<UserProject> addUserProject(@RequestBody Map<String, Long> payload) {
        UserProject savedUserProject = adminService.addUserProject(payload);
        return ResponseEntity.ok(savedUserProject);
    }

    @GetMapping("/getUsersProjects")
    public ResponseEntity<?> getUsersProjects() {
        List<UserProject> usersProjects = adminService.getUsersProjects();
        return ResponseEntity.ok(usersProjects);
    }

}
