package com.gabriel.iosefbinica.spring.security.jwt.services;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Timesheet;
import com.gabriel.iosefbinica.spring.security.jwt.repository.TimesheetRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManagerService {
    TimesheetRepository timesheetRepository;

    public ManagerService(TimesheetRepository timesheetRepository) {
        this.timesheetRepository = timesheetRepository;
    }


    public List<Timesheet> findSummaryAll() {
        return timesheetRepository.findAll();
    }

//    public List<Map<String, Object>> findSummaryAll() {
//        List<Timesheet> timesheets = timesheetRepository.findAll();
//
//        // Create a map to store weekly summaries
//        Map<LocalDate, Map<String, Object>> weeklySummaries = new HashMap<>();
//
//        // Iterate over each timesheet entry
//        for (Timesheet timesheet : timesheets) {
//            LocalDate selectedDate = timesheet.getSelectedDate();
//
//            // Determine the start and end dates of the week
//            LocalDate weekStart = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
//            LocalDate weekEnd = weekStart.plusDays(6);
//
//            // Check if the weekly summary exists in the map
//            if (!weeklySummaries.containsKey(weekStart)) {
//                // Create a new weekly summary object
//                Map<String, Object> weeklySummary = new HashMap<>();
//                weeklySummary.put("userId", timesheet.getUser().getId());
//                weeklySummary.put("weekStartDate", weekStart);
//                weeklySummary.put("weekEndDate", weekEnd);
//                weeklySummary.put("days", new ArrayList<Timesheet>());
//
//                // Add the weekly summary to the map
//                weeklySummaries.put(weekStart, weeklySummary);
//            }
//
//            // Add the timesheet entry to the corresponding week's days
//            List<Timesheet> weekDays = (List<Timesheet>) weeklySummaries.get(weekStart).get("days");
//            weekDays.add(timesheet);
//        }
//
//        // Convert the map values to a list and return
//        return new ArrayList<>(weeklySummaries.values());
//    }
}
