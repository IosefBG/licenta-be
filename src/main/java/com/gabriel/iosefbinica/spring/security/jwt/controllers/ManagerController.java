package com.gabriel.iosefbinica.spring.security.jwt.controllers;

import com.gabriel.iosefbinica.spring.security.jwt.domains.Timesheet;
import com.gabriel.iosefbinica.spring.security.jwt.models.dtos.WeeklySummaryDTO;
import com.gabriel.iosefbinica.spring.security.jwt.services.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
public class ManagerController {


    ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<Timesheet>> getAllUsers() {
        return ResponseEntity.ok().body(this.managerService.findSummaryAll());
    }

}