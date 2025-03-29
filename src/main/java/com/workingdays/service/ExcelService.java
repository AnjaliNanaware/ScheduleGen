package com.workingdays.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.workingdays.model.WorkingDay;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Service class to handle Excel operations
 */
public class ExcelService {
    
    /**
     * Generate Excel file with working days
     */
    public void generateWorkingDaysExcel(List<WorkingDay> workingDays, String outputPath) throws IOException {
        // Create workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Working Days");
        
        // Create header row with styles
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        Row headerRow = sheet.createRow(0);
        Cell dateHeaderCell = headerRow.createCell(0);
        dateHeaderCell.setCellValue("Date");
        dateHeaderCell.setCellStyle(headerStyle);
        
        Cell dayHeaderCell = headerRow.createCell(1);
        dayHeaderCell.setCellValue("Day");
        dayHeaderCell.setCellStyle(headerStyle);
        
        // Populate data rows
        int rowNum = 1;
        for (WorkingDay workingDay : workingDays) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(workingDay.getFormattedDate());
            row.createCell(1).setCellValue(workingDay.getDayName());
        }
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        
        // Add summary information
        Row summaryRow = sheet.createRow(rowNum + 1);
        Cell summaryLabelCell = summaryRow.createCell(0);
        summaryLabelCell.setCellValue("Total Working Days:");
        
        Cell summaryValueCell = summaryRow.createCell(1);
        summaryValueCell.setCellValue(workingDays.size());
        
        CellStyle summaryStyle = workbook.createCellStyle();
        Font summaryFont = workbook.createFont();
        summaryFont.setBold(true);
        summaryStyle.setFont(summaryFont);
        summaryLabelCell.setCellStyle(summaryStyle);
        summaryValueCell.setCellStyle(summaryStyle);
        
        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }
}