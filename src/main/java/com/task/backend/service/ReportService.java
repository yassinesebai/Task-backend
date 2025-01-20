package com.task.backend.service;

import com.task.backend.dto.response.EmployeeDTO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final EmployeeService employeeService;

    public byte[] employeesReport() throws Exception {
            List<EmployeeDTO> employees = employeeService.getEmployees(
                    null, null, null, null, null, null, null
            );

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Employees");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Employee ID", "First Name", "Last Name", "Job Title", "Department", "Hire Date", "Employment Status", "Email", "Address"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }

            int rowIndex = 1;
            for (EmployeeDTO employee : employees) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(employee.getEmployeeId());
                row.createCell(1).setCellValue(employee.getFirstName());
                row.createCell(2).setCellValue(employee.getLastName());
                row.createCell(3).setCellValue(employee.getJobTitle());
                row.createCell(4).setCellValue(employee.getDepartment());
                row.createCell(5).setCellValue(employee.getHireDate() != null ? employee.getHireDate().toString() : "");
                row.createCell(6).setCellValue(employee.getEmploymentStatus().toString());
                row.createCell(7).setCellValue(employee.getContactInformation());
                row.createCell(8).setCellValue(employee.getAddress());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
