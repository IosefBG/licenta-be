package com.gabriel.iosefbinica.spring.security.jwt.models.dtos;

import java.time.LocalDate;

public class WeeklySummaryDTO {
    private LocalDate weekStart;
    private int totalHours;

    public WeeklySummaryDTO(LocalDate weekStart, int totalHours) {
        this.weekStart = weekStart;
        this.totalHours = totalHours;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(LocalDate weekStart) {
        this.weekStart = weekStart;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }
}
