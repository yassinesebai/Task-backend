package com.task.backend.services;

import com.task.backend.dto.request.EmployeeUpdateDTO;
import com.task.backend.dto.response.EmployeeDTO;
import com.task.backend.model.Employee;
import com.task.backend.model.EmploymentStatus;
import com.task.backend.repository.EmployeeRepository;
import com.task.backend.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeDTO employeeDTO;
    private Employee employee;
    private EmployeeUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId("EMP12345");
        employeeDTO.setFirstName("John");
        employeeDTO.setLastName("Doe");
        employeeDTO.setJobTitle("Developer");
        employeeDTO.setDepartment("IT");
        employeeDTO.setHireDate(LocalDate.now());
        employeeDTO.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employeeDTO.setContactInformation("john.doe@example.com");
        employeeDTO.setAddress("123 Main St");

        employee = new Employee();
        // Set same values as DTO
        employee.setEmployeeId("EMP12345");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setJobTitle("Developer");
        employee.setDepartment("IT");
        employee.setHireDate(LocalDate.now());
        employee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employee.setContactInformation("john.doe@example.com");
        employee.setAddress("123 Main St");

        updateDTO = new EmployeeUpdateDTO();
        updateDTO.setFirstName("Jane");
        updateDTO.setLastName("Doe");
        updateDTO.setJobTitle("Senior Developer");
        updateDTO.setDepartment("IT");
        updateDTO.setHireDate(LocalDate.now());
        updateDTO.setEmploymentStatus(EmploymentStatus.ACTIVE);
        updateDTO.setContactInformation("jane.doe@example.com");
        updateDTO.setAddress("456 Oak St");
    }

    @Test
    void createEmployee_Success() {
        when(employeeRepository.existsByEmployeeId(anyString())).thenReturn(false);
        when(modelMapper.map(any(EmployeeDTO.class), eq(Employee.class))).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(modelMapper.map(any(Employee.class), eq(EmployeeDTO.class))).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.createEmployee(employeeDTO);

        assertNotNull(result);
        assertEquals(employeeDTO.getEmployeeId(), result.getEmployeeId());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_DuplicateId_ThrowsException() {
        when(employeeRepository.existsByEmployeeId(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(employeeDTO));
    }

    @Test
    void getEmployees_WithFilters() {
        List<Employee> employeeList = Collections.singletonList(employee);
        when(employeeRepository.findAll(any(Specification.class))).thenReturn(employeeList);
        when(modelMapper.map(any(Employee.class), eq(EmployeeDTO.class))).thenReturn(employeeDTO);

        List<EmployeeDTO> result = employeeService.getEmployees(
                "John", "Doe", "IT", "Developer",
                EmploymentStatus.ACTIVE, "EMP12345", LocalDate.now()
        );

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(employeeRepository).findAll(any(Specification.class));
    }

    @Test
    void updateEmployeeRH_Success() {
        when(employeeRepository.findByEmployeeId(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(modelMapper.map(any(Employee.class), eq(EmployeeDTO.class))).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.updateEmployeeRH("EMP12345", updateDTO);

        assertNotNull(result);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void updateEmployeeRH_NotFound_ThrowsException() {
        when(employeeRepository.findByEmployeeId(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployeeRH("EMP12345", updateDTO));
    }

    @Test
    void updateEmployeeManager_Success() {
        when(employeeRepository.findByEmployeeId(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(modelMapper.map(any(Employee.class), eq(EmployeeDTO.class))).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.updateEmployeeManager(
                "EMP12345",
                "Senior Developer",
                "jane.doe@example.com",
                "456 Oak St"
        );

        assertNotNull(result);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void updateEmployeeManager_NotFound_ThrowsException() {
        when(employeeRepository.findByEmployeeId(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployeeManager(
                "EMP12345",
                "Senior Developer",
                "jane.doe@example.com",
                "456 Oak St"
        ));
    }

    @Test
    void deleteEmployee_Success() {
        when(employeeRepository.findByEmployeeId(anyString())).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(any(Employee.class));

        employeeService.deleteEmployee("EMP12345");

        verify(employeeRepository).delete(any(Employee.class));
    }

    @Test
    void deleteEmployee_NotFound_ThrowsException() {
        when(employeeRepository.findByEmployeeId(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> employeeService.deleteEmployee("EMP12345"));
    }
}