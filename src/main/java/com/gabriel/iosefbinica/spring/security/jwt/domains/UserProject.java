package com.gabriel.iosefbinica.spring.security.jwt.domains;

import javax.persistence.*;

@Entity
@Table(name = "user_projects")
public class UserProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userProjectId;

    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "projectid")
    private Project project;

    public UserProject() {
    }

    public UserProject(User user, Project project) {
        this.user = user;
        this.project = project;
    }

    public Long getUserProjectId() {
        return userProjectId;
    }

    public void setUserProjectId(Long userProjectId) {
        this.userProjectId = userProjectId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
