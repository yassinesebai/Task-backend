package com.task.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "AUDIT_TRAIL")
public class AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String action; // CREATE, UPDATE...
    private String entityName; // table name
    private String entityId; // the ID (PRIMARY KEY) of the record
    private String modifiedBy; // who made the change
    private LocalDateTime timestamp;

    @Column(columnDefinition = "CLOB")
    private String changes; // JSON string representation of changes including old and new values
}

