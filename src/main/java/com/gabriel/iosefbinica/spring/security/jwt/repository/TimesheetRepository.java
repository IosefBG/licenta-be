package com.gabriel.iosefbinica.spring.security.jwt.repository;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
}
