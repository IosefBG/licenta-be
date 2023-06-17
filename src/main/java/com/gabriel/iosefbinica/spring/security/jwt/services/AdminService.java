package com.gabriel.iosefbinica.spring.security.jwt.services;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Project;
import com.gabriel.iosefbinica.spring.security.jwt.domains.Role;
import com.gabriel.iosefbinica.spring.security.jwt.domains.User;
import com.gabriel.iosefbinica.spring.security.jwt.domains.UserProject;
import com.gabriel.iosefbinica.spring.security.jwt.models.ERole;
import com.gabriel.iosefbinica.spring.security.jwt.models.dtos.UserWithMissingRoles;
import com.gabriel.iosefbinica.spring.security.jwt.models.dtos.UserWithRolesDTO;
import com.gabriel.iosefbinica.spring.security.jwt.repository.ProjectRepository;
import com.gabriel.iosefbinica.spring.security.jwt.repository.RoleRepository;
import com.gabriel.iosefbinica.spring.security.jwt.repository.UserProjectRepository;
import com.gabriel.iosefbinica.spring.security.jwt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final RoleRepository roleRepository;
    private final UserProjectRepository userProjectRepository;

    public AdminService(UserRepository userRepository, ProjectRepository projectRepository,
                        RoleRepository roleRepository, UserProjectRepository userProjectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.roleRepository = roleRepository;
        this.userProjectRepository = userProjectRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersWithUserRole() {
        return userRepository.findByRolesName(ERole.ROLE_USER);
    }

    public void addProject(String projectName, Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Error: Manager not found"));

        Project project = new Project();
        project.setProjectName(projectName);
        project.setManager(manager);

        projectRepository.save(project);
    }

    public List<User> getManagers() {
        return userRepository.findByRolesName(ERole.ROLE_MANAGER);
    }

    public List<Map<String, Object>> getManagersWithProjects() {
        List<User> managers = userRepository.findByRolesName(ERole.ROLE_MANAGER);
        List<Map<String, Object>> managersWithProjects = new ArrayList<>();

        for (User manager : managers) {
            List<Project> projects = projectRepository.findByManagerId(manager.getId());
            List<Map<String, Object>> projectList = new ArrayList<>();

            for (Project project : projects) {
                Map<String, Object> projectData = new HashMap<>();
                projectData.put("projectId", project.getId());
                projectData.put("projectName", project.getProjectName());
                projectData.put("managerId", manager.getId());
                projectData.put("managerUsername", manager.getUsername());
                projectList.add(projectData);
            }

            managersWithProjects.addAll(projectList);
        }

        return managersWithProjects;
    }

    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public List<UserWithMissingRoles> getAllUsersWithMissingRoles() {
        List<User> users = userRepository.findAll();
        List<UserWithMissingRoles> usersWithMissingRoles = new ArrayList<>();

        for (User user : users) {
            List<Role> missingRoles = roleRepository.findMissingRolesById(user.getId());
            UserWithMissingRoles userWithMissingRoles = new UserWithMissingRoles(user, missingRoles);
            usersWithMissingRoles.add(userWithMissingRoles);
        }

        return usersWithMissingRoles;
    }

    public void addRoleForUserId(Long userId, Integer roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Error: Role not found"));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    public void deleteRoleForUserId(Long userId, Integer roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Error: Role not found"));

        user.getRoles().remove(role);
        userRepository.save(user);
    }

    public List<UserWithRolesDTO> getUsersWithRoles() {
        List<User> users = userRepository.findAll();
        List<UserWithRolesDTO> usersWithRoles = new ArrayList<>();

        for (User user : users) {
            UserWithRolesDTO userWithRoles = new UserWithRolesDTO(user);
            usersWithRoles.add(userWithRoles);
        }

        return usersWithRoles;
    }

    public UserProject addUserProject(Map<String, Long> payload) {
        Long userId = payload.get("userId");
        Long projectId = payload.get("projectId");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ErrorMessage.USER_NOT_FOUND"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("ErrorMessage.PROJECT_NOT_FOUND"));

        // Check if the user is already added to the project
        UserProject existingUserProject = userProjectRepository.findByUserAndProject(user, project);
        if (existingUserProject != null) {
            throw new RuntimeException("ErrorMessage.USER_ALREADY_ADDED");
        }

        UserProject userProject = new UserProject();
        userProject.setUser(user);
        userProject.setProject(project);

        return userProjectRepository.save(userProject);
    }



    public List<UserProject> getUsersProjects() {
        return userProjectRepository.findAll();
    }

}
