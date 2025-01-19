package com.task.backend.repository;

import com.task.backend.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    UserRole findByName(String name);
}
