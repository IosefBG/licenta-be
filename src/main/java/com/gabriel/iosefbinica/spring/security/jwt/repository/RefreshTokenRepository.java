package com.gabriel.iosefbinica.spring.security.jwt.repository;
import java.util.Optional;

import com.gabriel.iosefbinica.spring.security.jwt.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.gabriel.iosefbinica.spring.security.jwt.domains.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  @Modifying
  int deleteByUser(User user);
}
