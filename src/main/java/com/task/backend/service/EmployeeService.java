package com.task.backend.service;

import com.task.backend.dto.request.EmployeeUpdateDTO;
import com.task.backend.dto.response.EmployeeDTO;
import com.task.backend.model.Employee;
import com.task.backend.model.EmploymentStatus;
import com.task.backend.repository.EmployeeRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        if (employeeRepository.existsByEmployeeId(employeeDTO.getEmployeeId())) {
            throw new IllegalArgumentException("Employee with this ID already exists.");
        }

        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        employee.setId(null); // Ensure ID is auto-generated
        Employee savedEmployee = employeeRepository.save(employee);
        return modelMapper.map(savedEmployee, EmployeeDTO.class);
    }

    public List<EmployeeDTO> getEmployees(
            String firstName,
            String lastName,
            String department,
            String jobTitle,
            EmploymentStatus employmentStatus,
            String employeeId,
            LocalDate hireDate) {

        List<Employee> employees = employeeRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (firstName != null) {
                predicates.add(criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%"));
            }
            if (lastName != null) {
                predicates.add(criteriaBuilder.like(root.get("lastName"), "%" + lastName + "%"));
            }
            if (department != null) {
                predicates.add(criteriaBuilder.equal(root.get("department"), department));
            }
            if (jobTitle != null) {
                predicates.add(criteriaBuilder.equal(root.get("jobTitle"), jobTitle));
            }
            if (employmentStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("employmentStatus"), employmentStatus));
            }
            if (employeeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("employeeId"), employeeId));
            }
            if (hireDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("hireDate"), hireDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        return employees.stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    public EmployeeDTO updateEmployeeRH(String employeeId, EmployeeUpdateDTO updateDTO) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found."));

        modelMapper.map(updateDTO, employee);
        Employee updatedEmployee = employeeRepository.save(employee);

        return modelMapper.map(updatedEmployee, EmployeeDTO.class);
    }

    public EmployeeDTO updateEmployeeManager(String employeeId,
                                             String jobTitle,
                                             String contactInformation,
                                             String address) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found."));

        // Map only allowed fields for managers
        employee.setJobTitle(jobTitle);
        employee.setContactInformation(contactInformation);
        employee.setAddress(address);

        Employee updatedEmployee = employeeRepository.save(employee);
        return modelMapper.map(updatedEmployee, EmployeeDTO.class);
    }


    public void deleteEmployee(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found."));
        employeeRepository.delete(employee);
    }
}
