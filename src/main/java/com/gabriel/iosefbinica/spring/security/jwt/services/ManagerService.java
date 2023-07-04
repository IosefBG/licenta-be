package com.gabriel.iosefbinica.spring.security.jwt.services;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Timesheet;
import com.gabriel.iosefbinica.spring.security.jwt.repository.TimesheetRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ManagerService {
    TimesheetRepository timesheetRepository;

    public ManagerService(TimesheetRepository timesheetRepository) {
        this.timesheetRepository = timesheetRepository;
    }


    public List<Timesheet> findSummaryAll() {
        return timesheetRepository.findAll();
    }

}
