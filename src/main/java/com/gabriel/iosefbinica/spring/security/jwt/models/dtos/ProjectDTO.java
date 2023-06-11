package com.gabriel.iosefbinica.spring.security.jwt.models.dtos;

public class ProjectDTO {
    private Long id;
    private String projectName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
