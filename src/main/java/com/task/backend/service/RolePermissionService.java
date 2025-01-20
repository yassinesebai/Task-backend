package com.task.backend.service;

import com.task.backend.dto.request.UserRoleDTO;
import com.task.backend.dto.response.PermissionDTO;
import com.task.backend.model.Permission;
import com.task.backend.model.RolePermission;
import com.task.backend.model.UserRole;
import com.task.backend.repository.PermissionRepository;
import com.task.backend.repository.UserManagementRepository;
import com.task.backend.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolePermissionService {
    private static final Logger logger = LoggerFactory.getLogger(RolePermissionService.class);
    private final ModelMapper modelMapper;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserManagementRepository userManagementRepository;

    @Transactional
    public void addRole(UserRoleDTO roleDTO) {
        UserRole userRole = new UserRole();
        userRole.setName(roleDTO.getName());

        List<RolePermission> rolePermissions = roleDTO.getPermissions().stream()
                .map(permissionId -> {
                    Permission permission = permissionRepository.findById(permissionId)
                            .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionId));
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRole(userRole);
                    rolePermission.setPermission(permission);
                    return rolePermission;
                }).toList();

        userRole.setRolePermissions(rolePermissions);

        userRoleRepository.save(userRole);
    }


    public List<UserRoleDTO> getAllRoles() {
        List<UserRole> roles = userRoleRepository.findAll();
        return roles.stream()
                .map(role -> {
                    UserRoleDTO userRoleDTO = new UserRoleDTO(role.getId(), role.getName());
                    userRoleDTO.setPermissions(role.getRolePermissions()
                            .stream()
                            .map(rolePermission ->
                                    rolePermission.getPermission().getAction() + " " + rolePermission.getPermission().getTableName())
                            .toList());
                    return userRoleDTO;
                })
                .toList();
    }

    public List<PermissionDTO> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(permission -> new PermissionDTO(permission.getPermissionId(),
                        permission.getAction() + " " + permission.getTableName()))
                .toList();
    }
}
