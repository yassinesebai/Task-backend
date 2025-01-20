package com.task.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_MANAGEMENT")
public class UserManagement {
    @Id
    @Column(name = "USER_CODE", nullable = false, unique = true)
    private String userCode;

    @Column(name = "FIRST_NAME", length = 30)
    private String firstName;

    @Column(name = "LAST_NAME", length = 30)
    private String lastName;

    @Column(name = "USER_PASSWORD")
    private String password;

    @Column(name = "BLOCK_ACCESS", length = 1)
    private Character blockAccess = 'N';

    @Column(unique = true, name = "EMAIL", length = 100)
    private String email;

    private Date createdAt = new Date();

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private UserRole role;

    @Column(name = "department", length = 100)
    private String department;
}
