package com.gabriel.iosefbinica.spring.security.jwt.repository;

import com.gabriel.iosefbinica.spring.security.jwt.domains.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    @Query("SELECT u FROM User u WHERE u NOT IN (SELECT up.user FROM UserProject up)")
    List<Map<String, Object>> getUsersProjects();
}
