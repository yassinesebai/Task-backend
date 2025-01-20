package com.task.backend.controller;

import com.task.backend.dto.request.EmployeeUpdateDTO;
import com.task.backend.dto.response.EmployeeDTO;
import com.task.backend.dto.response.GlobalResponse;
import com.task.backend.model.EmploymentStatus;
import com.task.backend.service.EmployeeService;
import com.task.backend.utils.GlobalVars;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
@SecurityRequirement(name = "Bearer Authentication")
public class EmployeeController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    @PostMapping("/add")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'EMPLOYEE', 'CREATE')")
    public GlobalResponse<EmployeeDTO> createEmployee(@RequestBody @Valid EmployeeDTO employeeDTO, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            result.getAllErrors().forEach(error -> errorMsg.append(error.getDefaultMessage()).append("; "));
            return new GlobalResponse<>(GlobalVars.INVALID_ACTION, "Validation failed: " + errorMsg);
        }
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
        return new GlobalResponse<>(GlobalVars.OK, "Employee created successfully", createdEmployee);
    }

    @GetMapping("/filter")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'EMPLOYEE', 'READ')")
    public GlobalResponse<List<EmployeeDTO>> viewEmployees(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String employmentStatus,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) LocalDate hireDate) {
        List<EmployeeDTO> employees = employeeService.getEmployees(
                firstName,
                lastName,
                department,
                jobTitle,
                employmentStatus != null ? EmploymentStatus.valueOf(employmentStatus) : null,
                employeeId,
                hireDate
        );
        return new GlobalResponse<>(GlobalVars.OK, "Employees retrieved successfully", employees);
    }

    // I hardcoded the update logic for the "MANAGER" role to allow editing only specific columns
    // (firstName, lastName, and jobTitle) in the Employee table. Since roles and permissions
    // are managed dynamically, I couldn't set up a more flexible solution in time.
    // Ideally, we could extend the permissions system to manage column-level access for each table,
    // but for now, this is a temporary solution that works
    // TODO: - improve permissions management to support column-level access for roles
    @PutMapping("/update/{employeeId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'EMPLOYEE', 'UPDATE')")
    public GlobalResponse<EmployeeDTO> updateEmployee(
            @PathVariable String employeeId,
            @RequestBody @Valid EmployeeUpdateDTO updateDTO,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
                StringBuilder errorMsg = new StringBuilder();
                result.getAllErrors().forEach(error -> errorMsg.append(error.getDefaultMessage()).append("; "));
                return new GlobalResponse<>(GlobalVars.INVALID_ACTION, "Validation failed: " + errorMsg);
            }
            String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().findFirst().orElseThrow(() -> new IllegalArgumentException("No role found")).getAuthority();

            log.info("User role: {}", role);
            EmployeeDTO updatedEmployee;

            if (role.equalsIgnoreCase("RH")) {
                updatedEmployee = employeeService.updateEmployeeRH(employeeId, updateDTO);
            } else if (role.equalsIgnoreCase("MANAGER")) {
                updatedEmployee = employeeService.updateEmployeeManager(employeeId,
                        updateDTO.getJobTitle(),
                        updateDTO.getContactInformation(),
                        updateDTO.getAddress());
            } else {
                return new GlobalResponse<>(
                        GlobalVars.INVALID_ACTION, "Invalid role specified."
                );
            }
            return new GlobalResponse<>(GlobalVars.OK, "Employee updated successfully", updatedEmployee);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new GlobalResponse<>(
                    GlobalVars.FAILED,
                    "Failed to update employee:" + ex.getMessage()
            );
        }
    }

    @DeleteMapping("/delete/{employeeId}")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'EMPLOYEE', 'DELETE')")
    public GlobalResponse<Void> deleteEmployee(@PathVariable String employeeId) {
        employeeService.deleteEmployee(employeeId);
        return new GlobalResponse<>(GlobalVars.OK, "Employee deleted successfully");
    }
}
