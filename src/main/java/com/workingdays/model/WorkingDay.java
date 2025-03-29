package com.workingdays.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Model class representing a working day
 */
public class WorkingDay {
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    
    public WorkingDay(LocalDate date) {
        this.date = date;
        this.dayOfWeek = date.getDayOfWeek();
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd MM yyyy"));
    }
    
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    
    public String getDayName() {
        return dayOfWeek.toString().charAt(0) + dayOfWeek.toString().substring(1).toLowerCase();
    }
    
    @Override
    public String toString() {
        return getFormattedDate() + " - " + getDayName();
    }
}