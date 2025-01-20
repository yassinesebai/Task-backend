package com.task.backend.repository;

import com.task.backend.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {
    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END " +
            "FROM RolePermission rp " +
            "JOIN UserRole r ON rp.role.id = r.id " +
            "JOIN Permission p ON rp.permission.permissionId = p.permissionId " +
            "WHERE r.name = :roleName AND p.tableName = :tableName AND p.action = :action")
    boolean existsByRoleAndPermission(@Param("roleName") String roleName,
                                      @Param("tableName") String tableName,
                                      @Param("action") String action);}
