package com.gabriel.iosefbinica.spring.security.jwt.repository;

import java.util.Optional;

import com.gabriel.iosefbinica.spring.security.jwt.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
