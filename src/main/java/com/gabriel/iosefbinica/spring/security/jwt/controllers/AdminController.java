package com.gabriel.iosefbinica.spring.security.jwt.controllers;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Project;
import com.gabriel.iosefbinica.spring.security.jwt.domains.User;
import com.gabriel.iosefbinica.spring.security.jwt.models.ERole;
import com.gabriel.iosefbinica.spring.security.jwt.repository.ProjectRepository;
import com.gabriel.iosefbinica.spring.security.jwt.repository.RoleRepository;
import com.gabriel.iosefbinica.spring.security.jwt.repository.UserRepository;
import com.gabriel.iosefbinica.spring.security.jwt.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    UserRepository userRepository;
    ProjectRepository projectRepository;
    RoleRepository roleRepository;
    AdminService adminService;

    public AdminController(UserRepository userRepository, ProjectRepository projectRepository, RoleRepository roleRepository, AdminService adminService) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.roleRepository = roleRepository;
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/addProject")
    public ResponseEntity<?> addProject(@RequestParam String projectName, @RequestParam Long managerId) {
        User manager = userRepository.findById(managerId).orElseThrow(() -> new RuntimeException("Error: Manager not found"));
        Project project = new Project();
        project.setProjectName(projectName);
        project.setManager(manager);
        projectRepository.save(project);
        return ResponseEntity.ok("Project added");
    }

    @GetMapping("/getManagers")
    public ResponseEntity<?> getManagers() {
        List<User> managers = userRepository.findByRolesName(ERole.ROLE_MANAGER);
        return ResponseEntity.ok(managers);
    }

    @GetMapping("/getManagersWithProjects")
    public ResponseEntity<?> getManagersWithProjects() {
//        List<ManagerWithProjectsDTO> managersWithProjects = adminService.getManagersWithProjects();
        List<Map<String,Object>> managersWithProjects = adminService.getManagersWithProjects();
        return ResponseEntity.ok(managersWithProjects);
    }


}