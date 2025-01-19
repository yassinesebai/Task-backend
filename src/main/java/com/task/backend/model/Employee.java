package com.task.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EMPLOYEE")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false, unique = true)
    private String id;

    @Column(name = "FIRST_NAME", length = 50, nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 50, nullable = false)
    private String lastName;

    @Column(name = "EMPLOYEE_ID", unique = true, nullable = false, length = 50)
    private String employeeId;

    @Column(name = "JOB_TITLE", length = 100, nullable = false)
    private String jobTitle;

    @Column(name = "DEPARTMENT", length = 100, nullable = false)
    private String department;

    @Column(name = "HIRE_DATE", nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "EMPLOYMENT_STATUS", nullable = false, length = 10)
    private EmploymentStatus employmentStatus;

    @Column(name = "CONTACT_INFORMATION", length = 150)
    private String contactInformation;

    @Column(name = "ADDRESS")
    private String address;
}