package com.workingdays.main;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.workingdays.model.WorkingDay;
import com.workingdays.service.DatabaseService;
import com.workingdays.service.ExcelService;
import com.workingdays.service.HolidayService;
import com.workingdays.service.WorkingDayCalculator;

public class WorkingDaysGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Get date range
        System.out.println("=== Working Days Generator ===");
        System.out.println("Please enter start date (YYYY-MM-DD):");
        String startDateStr = scanner.nextLine();
        
        System.out.println("Please enter end date (YYYY-MM-DD):");
        String endDateStr = scanner.nextLine();
        
        // Get working days
        Set<DayOfWeek> workingDays = getWorkingDaysInput(scanner);
        
        // Get holidays
        System.out.println("Do you want to load holidays from Excel file or manually input them?");
        System.out.println("1. Load from Excel file");
        System.out.println("2. Input manually");
        int holidayChoice = Integer.parseInt(scanner.nextLine());
        
        HolidayService holidayService = new HolidayService();
        if (holidayChoice == 1) {
            System.out.println("Enter the path to the Excel file containing holidays:");
            String excelPath = scanner.nextLine();
            File excelFile = new File(excelPath);
            
            if (!excelFile.exists()) {
                System.out.println("File not found. Please check the path and try again.");
                return;
            }
            
            // Upload to database and then load holidays
            DatabaseService dbService = new DatabaseService();
            try {
                dbService.uploadHolidaysFromExcel(excelFile);
                System.out.println("Holidays uploaded to database successfully!");
                holidayService.loadHolidaysFromDatabase();
            } catch (Exception e) {
                System.out.println("Error processing holidays: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("Enter holidays (YYYY-MM-DD), one per line. Enter 'done' when finished:");
            String input;
            while (!(input = scanner.nextLine()).equalsIgnoreCase("done")) {
                try {
                    LocalDate holiday = LocalDate.parse(input);
                    holidayService.addHoliday(holiday);
                } catch (Exception e) {
                    System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
                }
            }
        }
        
        // Calculate working days
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            
            WorkingDayCalculator calculator = new WorkingDayCalculator(workingDays, holidayService.getHolidays());
            List<WorkingDay> workingDaysList = calculator.calculateWorkingDays(startDate, endDate);
            
            // Generate Excel output
            System.out.println("Enter output Excel file path:");
            String outputPath = scanner.nextLine();
            
            ExcelService excelService = new ExcelService();
            excelService.generateWorkingDaysExcel(workingDaysList, outputPath);
            
            System.out.println("Excel file generated successfully at: " + outputPath);
            System.out.println("Total working days: " + workingDaysList.size());
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    private static Set<DayOfWeek> getWorkingDaysInput(Scanner scanner) {
        Set<DayOfWeek> workingDays = new HashSet<>();
        Map<Integer, DayOfWeek> dayOptions = new HashMap<>();
        
        dayOptions.put(1, DayOfWeek.MONDAY);
        dayOptions.put(2, DayOfWeek.TUESDAY);
        dayOptions.put(3, DayOfWeek.WEDNESDAY);
        dayOptions.put(4, DayOfWeek.THURSDAY);
        dayOptions.put(5, DayOfWeek.FRIDAY);
        dayOptions.put(6, DayOfWeek.SATURDAY);
        dayOptions.put(7, DayOfWeek.SUNDAY);
        
        System.out.println("Select working days (enter numbers separated by space):");
        System.out.println("1. Monday");
        System.out.println("2. Tuesday");
        System.out.println("3. Wednesday");
        System.out.println("4. Thursday");
        System.out.println("5. Friday");
        System.out.println("6. Saturday");
        System.out.println("7. Sunday");
        
        String[] selections = scanner.nextLine().split(" ");
        for (String selection : selections) {
            try {
                int dayOption = Integer.parseInt(selection.trim());
                if (dayOptions.containsKey(dayOption)) {
                    workingDays.add(dayOptions.get(dayOption));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + selection + ". Skipping.");
            }
        }
        
        return workingDays;
    }
}