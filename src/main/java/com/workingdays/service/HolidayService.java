package com.workingdays.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Service class to manage holidays
 */
public class HolidayService {
    private Set<LocalDate> holidays;
    
    public HolidayService() {
        this.holidays = new HashSet<>();
    }
    
    public void addHoliday(LocalDate date) {
        holidays.add(date);
    }
    
    public Set<LocalDate> getHolidays() {
        return holidays;
    }
    
    /**
     * Load holidays from the database
     */
    public void loadHolidaysFromDatabase() {
        // Create database service to load holidays
        DatabaseService dbService = new DatabaseService();
        try {
            this.holidays = dbService.loadHolidays();
        } catch (Exception e) {
            System.out.println("Error loading holidays from database: " + e.getMessage());
        }
    }
}