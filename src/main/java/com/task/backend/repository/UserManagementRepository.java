package com.task.backend.repository;

import com.task.backend.model.UserManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserManagementRepository extends JpaRepository<UserManagement, String> {
    @Query("SELECT u FROM UserManagement u WHERE LOWER(u.userCode) = LOWER(:username)")
    Optional<UserManagement> findByUserCodeIgnoreCase(@Param("username") String username);
}
