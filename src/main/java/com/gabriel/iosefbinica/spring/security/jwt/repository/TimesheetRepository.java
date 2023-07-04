package com.gabriel.iosefbinica.spring.security.jwt.repository;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Timesheet;
import com.gabriel.iosefbinica.spring.security.jwt.domains.User;
import com.gabriel.iosefbinica.spring.security.jwt.domains.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    void deleteByUserAndProjectAndSelectedDateBetween(User user, UserProject project, LocalDate fromDate, LocalDate toDate);

    Optional<Timesheet> findByUserAndProjectAndSelectedDate(User user, UserProject project, LocalDate parse);


    @Query("SELECT t FROM Timesheet t WHERE t.user.id = :userId AND t.fromDate >= :weekStartDate AND t.toDate <= :weekEndDate")
    List<Timesheet> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("weekStartDate") LocalDate weekStartDate,
            @Param("weekEndDate") LocalDate weekEndDate
    );

//    @Query("SELECT t FROM Timesheet t WHERE t.user.id = :userId")
//    List<Timesheet> findByUserIdAndToDateBetween(@Param("userId") Long userId);

    @Query("SELECT t FROM Timesheet t WHERE t.user.id = :userId AND t.selectedDate BETWEEN :startDate AND :endDate")
    List<Timesheet> findByUserIdAndToDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Timesheet> findBySelectedDateBetween(LocalDate fromDate, LocalDate toDate);
}


