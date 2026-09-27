package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.batch;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.batch.infrastructure.item.ItemReader;

public class CustomerExcelReader implements ItemReader<CustomerRow> {

    private final Workbook workbook;
    private final Sheet sheet;

    private int row = 1;

    private final DataFormatter formatter = new DataFormatter();

    public CustomerExcelReader(String path) throws IOException {

        workbook = new XSSFWorkbook(
                new FileInputStream(path)
        );

        sheet = workbook.getSheetAt(0);
    }

    @Override
    public CustomerRow read() {

        while (row <= sheet.getLastRowNum()) {

            Row currentRow = sheet.getRow(row++);

            if (currentRow == null) {
                continue;
            }

            String email = text(currentRow.getCell(0));
            String name = text(currentRow.getCell(1));
            String phone = text(currentRow.getCell(2));

            if (email.isBlank() || name.isBlank()) {
                continue;
            }

            LocalDate dateOfBirth = date(currentRow.getCell(3));
            if (dateOfBirth == null) {
                continue;
            }

            return new CustomerRow(
                    email,
                    name,
                    phone,
                    dateOfBirth
            );
        }

        try {
            workbook.close();
        } catch (Exception ignored) {
        }

        return null;
    }

    private String text(Cell cell) {

        if (cell == null) {
            return "";
        }

        return formatter.formatCellValue(cell).trim();
    }

    private LocalDate date(Cell cell) {

        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC
                && org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        String value = formatter.formatCellValue(cell).trim();
        if (value.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value);
        } catch (Exception ignored) {
            return null;
        }
    }
}