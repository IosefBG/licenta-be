package com.gabriel.iosefbinica.spring.security.jwt.repository;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Project;
import com.gabriel.iosefbinica.spring.security.jwt.domains.User;
import com.gabriel.iosefbinica.spring.security.jwt.domains.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserProjectRepository extends JpaRepository<UserProject, Long> {

    List<UserProject> findByUserId(Long id);

    UserProject findByUserAndProject(User user, Project project);

    Optional<UserProject> findByProjectIdAndUserId(Long projectId, Long userId);
}
