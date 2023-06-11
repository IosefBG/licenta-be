package com.gabriel.iosefbinica.spring.security.jwt.repository;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByManagerId(Long id);
}

