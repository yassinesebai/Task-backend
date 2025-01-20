package com.task.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.task.backend.dto.request.EmployeeUpdateDTO;
import com.task.backend.dto.response.EmployeeDTO;
import com.task.backend.model.Employee;
import com.task.backend.model.EmploymentStatus;
import com.task.backend.repository.EmployeeRepository;
import com.task.backend.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Optional;

class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEmployee_ShouldThrowException_WhenEmployeeIdExists() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId("EMP10101");

        when(employeeRepository.existsByEmployeeId(employeeDTO.getEmployeeId())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(employeeDTO));
        assertEquals("Employee with this ID already exists.", exception.getMessage());

        verify(employeeRepository, times(1)).existsByEmployeeId(employeeDTO.getEmployeeId());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployee_ShouldSaveEmployeeSuccessfully_WhenValidDataProvided() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId("EMP10102");
        employeeDTO.setFirstName("Ali");
        employeeDTO.setLastName("Khan");
        employeeDTO.setJobTitle("Backend Engineer");
        employeeDTO.setDepartment("Tech");
        employeeDTO.setHireDate(LocalDate.of(2023, 6, 10));
        employeeDTO.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employeeDTO.setContactInformation("ali.khan@company.com");
        employeeDTO.setAddress("Downtown, Block 4, Cityville");

        Employee employee = new Employee();
        employee.setEmployeeId("EMP10102");
        employee.setFirstName("Ali");
        employee.setLastName("Khan");

        Employee savedEmployee = new Employee();
        savedEmployee.setId("3");
        savedEmployee.setEmployeeId("EMP10102");
        savedEmployee.setFirstName("Ali");
        savedEmployee.setLastName("Khan");

        when(employeeRepository.existsByEmployeeId(employeeDTO.getEmployeeId())).thenReturn(false);
        when(modelMapper.map(employeeDTO, Employee.class)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(savedEmployee);
        when(modelMapper.map(savedEmployee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.createEmployee(employeeDTO);

        assertNotNull(result);
        assertEquals("EMP10102", result.getEmployeeId());
        assertEquals("Ali", result.getFirstName());
        assertEquals("Khan", result.getLastName());

        verify(employeeRepository, times(1)).existsByEmployeeId(employeeDTO.getEmployeeId());
        verify(modelMapper, times(1)).map(employeeDTO, Employee.class);
        verify(employeeRepository, times(1)).save(employee);
        verify(modelMapper, times(1)).map(savedEmployee, EmployeeDTO.class);
    }

    @Test
    void updateEmployeeRH_ShouldUpdateAllFields() {
        Employee existingEmployee = new Employee();
        existingEmployee.setEmployeeId("EMP20205");
        existingEmployee.setFirstName("Rachid");
        existingEmployee.setLastName("Amrani");
        existingEmployee.setDepartment("Human Resources");

        EmployeeUpdateDTO updateDTO = new EmployeeUpdateDTO();
        updateDTO.setFirstName("Rachid Updated");
        updateDTO.setLastName("Amrani Updated");
        updateDTO.setDepartment("Finance");

        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmployeeId("EMP20205");
        updatedEmployee.setFirstName("Rachid Updated");
        updatedEmployee.setLastName("Amrani Updated");
        updatedEmployee.setDepartment("Finance");

        EmployeeDTO updatedEmployeeDTO = new EmployeeDTO();
        updatedEmployeeDTO.setEmployeeId("EMP20205");
        updatedEmployeeDTO.setFirstName("Rachid Updated");
        updatedEmployeeDTO.setLastName("Amrani Updated");
        updatedEmployeeDTO.setDepartment("Finance");

        when(employeeRepository.findByEmployeeId("EMP20205")).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);
        when(modelMapper.map(updateDTO, Employee.class)).thenReturn(existingEmployee);
        when(modelMapper.map(updatedEmployee, EmployeeDTO.class)).thenReturn(updatedEmployeeDTO);

        EmployeeDTO result = employeeService.updateEmployeeRH("EMP20205", updateDTO);

        assertNotNull(result);
        assertEquals("Rachid Updated", result.getFirstName());
        assertEquals("Finance", result.getDepartment());

        verify(employeeRepository, times(1)).findByEmployeeId("EMP20205");
        verify(employeeRepository, times(1)).save(existingEmployee);
        verify(modelMapper, times(1)).map(updateDTO, existingEmployee);
        verify(modelMapper, times(1)).map(updatedEmployee, EmployeeDTO.class);
    }

    @Test
    void updateEmployeeManager_ShouldUpdateAllowedFields() {
        Employee existingEmployee = new Employee();
        existingEmployee.setEmployeeId("EMP20206");
        existingEmployee.setFirstName("Hassan");
        existingEmployee.setLastName("Ben Salah");
        existingEmployee.setJobTitle("Junior Developer");
        existingEmployee.setContactInformation("hassan.bensalah@example.com");
        existingEmployee.setAddress("Rabat, Morocco");

        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmployeeId("EMP20206");
        updatedEmployee.setFirstName("Hassan");
        updatedEmployee.setLastName("Ben Salah");
        updatedEmployee.setJobTitle("Senior Developer");
        updatedEmployee.setContactInformation("hassan.bensalah@newexample.com");
        updatedEmployee.setAddress("Casablanca, Morocco");

        EmployeeDTO updatedEmployeeDTO = new EmployeeDTO();
        updatedEmployeeDTO.setEmployeeId("EMP20206");
        updatedEmployeeDTO.setFirstName("Hassan");
        updatedEmployeeDTO.setLastName("Ben Salah");
        updatedEmployeeDTO.setJobTitle("Senior Developer");
        updatedEmployeeDTO.setContactInformation("hassan.bensalah@newexample.com");
        updatedEmployeeDTO.setAddress("Casablanca, Morocco");

        when(employeeRepository.findByEmployeeId("EMP20206")).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);
        when(modelMapper.map(updatedEmployee, EmployeeDTO.class)).thenReturn(updatedEmployeeDTO);

        EmployeeDTO result = employeeService.updateEmployeeManager(
                "EMP20206",
                "Senior Developer",
                "hassan.bensalah@newexample.com",
                "Casablanca, Morocco"
        );

        assertNotNull(result);
        assertEquals("Senior Developer", result.getJobTitle());
        assertEquals("Casablanca, Morocco", result.getAddress());

        verify(employeeRepository, times(1)).findByEmployeeId("EMP20206");
        verify(employeeRepository, times(1)).save(existingEmployee);
        verify(modelMapper, times(1)).map(updatedEmployee, EmployeeDTO.class);
    }

    @Test
    void deleteEmployee_ShouldDeleteExistingEmployee() {
        String employeeId = "EMP20207";
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setFirstName("Ahmed");
        employee.setLastName("El Khattabi");
        employee.setJobTitle("Software Engineer");
        employee.setDepartment("IT");
        employee.setContactInformation("ahmed.elkhattabi@example.com");
        employee.setAddress("Marrakech, Morocco");

        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));

        // Act
        employeeService.deleteEmployee(employeeId);

        // Assert
        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void deleteEmployee_ShouldThrowExceptionWhenEmployeeNotFound() {
        String employeeId = "EMP99999";

        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                employeeService.deleteEmployee(employeeId));

        assertEquals("Employee not found.", exception.getMessage());
        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
        verify(employeeRepository, times(0)).delete(any(Employee.class));
    }
}
