package com.gabriel.iosefbinica.spring.security.jwt.repository;

import com.gabriel.iosefbinica.spring.security.jwt.domains.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserProjectRepository extends JpaRepository<UserProject, Long> {

    List<UserProject> findByUserId(Long id);
}
