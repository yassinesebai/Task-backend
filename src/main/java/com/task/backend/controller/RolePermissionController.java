package com.task.backend.controller;

import com.task.backend.dto.request.UserRoleDTO;
import com.task.backend.dto.response.GlobalResponse;
import com.task.backend.dto.response.PermissionDTO;
import com.task.backend.service.RolePermissionService;
import com.task.backend.utils.GlobalVars;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
@AllArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @PostMapping("/add")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'ROLE', 'CREATE')")
    public GlobalResponse<String> addRole(@RequestBody UserRoleDTO roleDTO) {
        try {
            rolePermissionService.addRole(roleDTO);
            return new GlobalResponse<>(GlobalVars.OK, "Role added successfully");
        } catch (Exception e) {
            return new GlobalResponse<>(GlobalVars.SERVER_ERROR, "Failed to add role: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'ROLE', 'READ')")
    public GlobalResponse<List<UserRoleDTO>> getRoles() {
        try {
            List<UserRoleDTO> roles = rolePermissionService.getAllRoles();
            return new GlobalResponse<>(GlobalVars.OK, "Roles retrieved successfully", roles);
        } catch (Exception e) {
            return new GlobalResponse<>(GlobalVars.SERVER_ERROR, "Failed to fetch roles: " + e.getMessage());
        }
    }

    @GetMapping("/permissions/all")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'PERMISSION', 'READ')")
    public GlobalResponse<List<PermissionDTO>> getPermissions() {
        try {
            List<PermissionDTO> permissions = rolePermissionService.getAllPermissions();
            return new GlobalResponse<>(GlobalVars.OK, "Permissions retrieved successfully", permissions);
        } catch (Exception e) {
            return new GlobalResponse<>(GlobalVars.SERVER_ERROR, "Failed to fetch permissions: " + e.getMessage());
        }
    }
}
