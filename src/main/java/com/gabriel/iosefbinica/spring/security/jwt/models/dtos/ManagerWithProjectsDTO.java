package com.gabriel.iosefbinica.spring.security.jwt.models.dtos;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Project;

import java.util.List;

public class ManagerWithProjectsDTO {
    private ManagerDTO manager;
    private List<Project> projects;

    public ManagerWithProjectsDTO(ManagerDTO managerDTO, List<Project> projects) {
        this.manager = managerDTO;
        this.projects = projects;
    }

    public ManagerDTO getManager() {
        return manager;
    }

    public void setManager(ManagerDTO manager) {
        this.manager = manager;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}

