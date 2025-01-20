package com.task.backend.services;

import com.task.backend.dto.response.EmployeeDTO;
import com.task.backend.model.EmploymentStatus;
import com.task.backend.service.EmployeeService;
import com.task.backend.service.ReportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void employeesReport_ShouldGenerateExcelFile() throws Exception {
        List<EmployeeDTO> mockEmployees = List.of(
                new EmployeeDTO(
                        "Ali", "El Mekki", "EMP001", "Software Engineer", "IT",
                        LocalDate.of(2021, 1, 15), EmploymentStatus.ACTIVE,
                        "ali.elmekki@example.com", "123 Boulevard Hassan II, Casablanca"
                ),
                new EmployeeDTO(
                        "Fatima", "Ouazzani", "EMP002", "HR Specialist", "HR",
                        LocalDate.of(2020, 9, 10), EmploymentStatus.ACTIVE,
                        "fatima.ouazzani@example.com", "456 Avenue Mohammed V, Rabat"
                )
        );
        when(employeeService.getEmployees(null, null, null, null, null, null, null)).thenReturn(mockEmployees);

        // Act
        byte[] reportBytes = reportService.employeesReport();

        assertNotNull(reportBytes);
        assertTrue(reportBytes.length > 0);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(reportBytes);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheet("Employees");
            assertNotNull(sheet);

            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow);
            String[] expectedHeaders = {"Employee ID", "First Name", "Last Name", "Job Title", "Department", "Hire Date", "Employment Status", "Email", "Address"};
            for (int i = 0; i < expectedHeaders.length; i++) {
                Cell cell = headerRow.getCell(i);
                assertNotNull(cell);
                assertEquals(expectedHeaders[i], cell.getStringCellValue());
            }

            Row firstEmployeeRow = sheet.getRow(1);
            assertNotNull(firstEmployeeRow);
            assertEquals("EMP001", firstEmployeeRow.getCell(0).getStringCellValue());
            assertEquals("Ali", firstEmployeeRow.getCell(1).getStringCellValue());
            assertEquals("El Mekki", firstEmployeeRow.getCell(2).getStringCellValue());
            assertEquals("Software Engineer", firstEmployeeRow.getCell(3).getStringCellValue());
            assertEquals("IT", firstEmployeeRow.getCell(4).getStringCellValue());
            assertEquals("2021-01-15", firstEmployeeRow.getCell(5).getStringCellValue());
            assertEquals("ACTIVE", firstEmployeeRow.getCell(6).getStringCellValue());
            assertEquals("ali.elmekki@example.com", firstEmployeeRow.getCell(7).getStringCellValue());
            assertEquals("123 Boulevard Hassan II, Casablanca", firstEmployeeRow.getCell(8).getStringCellValue());
        }
    }
}
