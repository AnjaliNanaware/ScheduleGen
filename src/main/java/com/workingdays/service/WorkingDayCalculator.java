package com.workingdays.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.workingdays.model.WorkingDay;

/**
 * Service class to calculate working days within a date range
 */
public class WorkingDayCalculator {
    private Set<DayOfWeek> workingDays;
    private Set<LocalDate> holidays;
    
    public WorkingDayCalculator(Set<DayOfWeek> workingDays, Set<LocalDate> holidays) {
        this.workingDays = workingDays;
        this.holidays = holidays;
    }
    
    /**
     * Calculate all working days within the given date range, excluding holidays
     */
    public List<WorkingDay> calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        List<WorkingDay> result = new ArrayList<>();
        
        // Validate date range
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        
        // Iterate through each date in the range
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // Check if the day is a working day and not a holiday
            if (workingDays.contains(currentDate.getDayOfWeek()) && !holidays.contains(currentDate)) {
                result.add(new WorkingDay(currentDate));
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        return result;
    }
}