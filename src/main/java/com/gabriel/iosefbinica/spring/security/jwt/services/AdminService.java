package com.gabriel.iosefbinica.spring.security.jwt.services;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Project;
import com.gabriel.iosefbinica.spring.security.jwt.domains.User;
import com.gabriel.iosefbinica.spring.security.jwt.models.ERole;
import com.gabriel.iosefbinica.spring.security.jwt.repository.ProjectRepository;
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

    public AdminService(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
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


//    public List<ManagerWithProjectsDTO> getManagersWithProjects() {
//        List<User> managers = userRepository.findByRolesName(ERole.ROLE_MANAGER);
//        List<ManagerWithProjectsDTO> managersWithProjects = new ArrayList<>();
//
//        for (User manager : managers) {
//            ManagerDTO managerDTO = new ManagerDTO(manager.getId(), manager.getUsername());
//            List<Project> projects = projectRepository.findByManagerId(manager.getId());
//
//            ManagerWithProjectsDTO managerWithProjects = new ManagerWithProjectsDTO(managerDTO, projects);
//            managersWithProjects.add(managerWithProjects);
//        }
//
//        return managersWithProjects;
//    }
}

