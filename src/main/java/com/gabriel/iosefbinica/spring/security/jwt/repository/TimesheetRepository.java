package com.gabriel.iosefbinica.spring.security.jwt.repository;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Timesheet;
import com.gabriel.iosefbinica.spring.security.jwt.domains.User;
import com.gabriel.iosefbinica.spring.security.jwt.domains.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    void deleteByUserAndProjectAndSelectedDateBetween(User user, UserProject project, LocalDate fromDate, LocalDate toDate);

    Optional<Timesheet> findByUserAndProjectAndSelectedDate(User user, UserProject project, LocalDate parse);
}
