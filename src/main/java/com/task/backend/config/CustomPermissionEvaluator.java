package com.task.backend.config;

import com.task.backend.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || permission == null) {
            return false;
        }

        // Get the logged-in user's role from Authentication object
        String roleName = authentication.getAuthorities().iterator().next().getAuthority();

        // Extract tableName and action from method parameters
        String tableName = targetDomainObject.toString();
        String action = permission.toString();

        // Check if the role has the specified permission
        return rolePermissionRepository.existsByRoleAndPermission(roleName, tableName, action);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
