package com.gabriel.iosefbinica.spring.security.jwt.repository;

import java.util.List;
import java.util.Optional;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gabriel.iosefbinica.spring.security.jwt.models.ERole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);

  Optional<Role> findById(Integer roleId);

  List<Role> findMissingRolesById(Long id);

}
