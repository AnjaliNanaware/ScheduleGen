package com.workingdays.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Service class to handle database operations related to holidays.
 */
public class DatabaseService {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/workingdays";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Anjali@9876";

    public DatabaseService() {
        initializeDatabase();
    }

    /**
     * Initializes the database schema if it doesn't exist.
     */
    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS holidays (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "holiday_date DATE NOT NULL UNIQUE)");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    /**
     * Establishes and returns a database connection.
     *
     * @return A database connection.
     * @throws SQLException If a database access error occurs.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Uploads holiday dates from an Excel file to the database.
     *
     * @param excelFile The Excel file containing holiday dates.
     * @throws Exception If an error occurs during file processing or database interaction.
     */
    public void uploadHolidaysFromExcel(File excelFile) throws Exception {
        try (Connection conn = getConnection();
             Statement clearStmt = conn.createStatement()) {

            clearStmt.execute("DELETE FROM holidays"); // Clear existing data
        }

        try (FileInputStream fis = new FileInputStream(excelFile);
             XSSFWorkbook workbook = new XSSFWorkbook(fis);
             Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO holidays (holiday_date) VALUES (?)")) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;

            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    Cell firstCell = row.getCell(0);
                    if (firstCell != null && firstCell.toString().equalsIgnoreCase("Date")) {
                        continue; // Skip header row
                    }
                }

                Cell dateCell = row.getCell(0);
                if (dateCell != null) {
                    LocalDate holidayDate = null;
                    try {
                        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(dateCell)) {
                            java.util.Date date = dateCell.getDateCellValue();
                            holidayDate = new java.sql.Date(date.getTime()).toLocalDate();
                        } else {
                            String dateStr = dateCell.toString().trim();
                            if (dateStr.matches("\\d{2} \\d{2} \\d{4}")) {
                                String[] parts = dateStr.split(" ");
                                holidayDate = LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
                            } else {
                                holidayDate = LocalDate.parse(dateStr);
                            }
                        }
                        if (holidayDate != null) {
                            pstmt.setDate(1, java.sql.Date.valueOf(holidayDate));
                            pstmt.executeUpdate();
                        }

                    } catch (Exception e) {
                        System.err.println("Skipping invalid date: " + dateCell.toString());
                    }
                }
            }
        }
    }

    /**
     * Loads holiday dates from the database.
     *
     * @return A set of LocalDate objects representing holiday dates.
     * @throws SQLException If a database access error occurs.
     */
    public Set<LocalDate> loadHolidays() throws SQLException {
        Set<LocalDate> holidays = new HashSet<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT holiday_date FROM holidays")) {

            while (rs.next()) {
                holidays.add(rs.getDate("holiday_date").toLocalDate());
            }
        }
        return holidays;
    }
}