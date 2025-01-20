package com.task.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.task.backend.dto.request.UserRoleDTO;
import com.task.backend.dto.response.PermissionDTO;
import com.task.backend.model.Permission;
import com.task.backend.model.RolePermission;
import com.task.backend.model.UserRole;
import com.task.backend.repository.PermissionRepository;
import com.task.backend.repository.UserRoleRepository;
import com.task.backend.service.RolePermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

class RolesAndPermissionsServiceTest {

    @InjectMocks
    private RolePermissionService rolePermissionService;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addRole_ShouldAddRoleWithPermissions() {
        // Arrange
        String permissionId1 = "PERM01";
        String permissionId2 = "PERM02";
        UserRoleDTO roleDTO = new UserRoleDTO(null, "Manager", List.of(permissionId1, permissionId2));

        Permission permission1 = new Permission(permissionId1, "EMPLOYEE", "READ", null);
        Permission permission2 = new Permission(permissionId2, "EMPLOYEE", "WRITE", null);

        when(permissionRepository.findById(permissionId1)).thenReturn(Optional.of(permission1));
        when(permissionRepository.findById(permissionId2)).thenReturn(Optional.of(permission2));

        // Act
        rolePermissionService.addRole(roleDTO);

        // Assert
        verify(userRoleRepository, times(1)).save(any(UserRole.class));
    }

    @Test
    void addRole_ShouldThrowExceptionWhenPermissionNotFound() {
        // Arrange
        String invalidPermissionId = "PERM99";
        UserRoleDTO roleDTO = new UserRoleDTO(null, "Manager", List.of(invalidPermissionId));

        when(permissionRepository.findById(invalidPermissionId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                rolePermissionService.addRole(roleDTO));

        assertEquals("Permission not found: PERM99", exception.getMessage());
        verify(userRoleRepository, times(0)).save(any(UserRole.class));
    }

    @Test
    void getAllRoles_ShouldReturnAllRolesWithPermissions() {
        // Arrange
        UserRole userRole1 = new UserRole("ROLE01", "Manager");
        UserRole userRole2 = new UserRole("ROLE02", "Employee");

        Permission permission1 = new Permission("PERM01", "EMPLOYEE", "READ", null);
        Permission permission2 = new Permission("PERM02", "EMPLOYEE", "WRITE", null);

        RolePermission rolePermission1 = new RolePermission("RP01", userRole1, permission1);
        RolePermission rolePermission2 = new RolePermission("RP02", userRole1, permission2);

        userRole1.setRolePermissions(List.of(rolePermission1, rolePermission2));
        userRole2.setRolePermissions(List.of());

        when(userRoleRepository.findAll()).thenReturn(List.of(userRole1, userRole2));

        // Act
        List<UserRoleDTO> roles = rolePermissionService.getAllRoles();

        // Assert
        assertEquals(2, roles.size());
        assertEquals("Manager", roles.get(0).getName());
        assertEquals(2, roles.get(0).getPermissions().size());
        assertEquals("Employee", roles.get(1).getName());
        assertTrue(roles.get(1).getPermissions().isEmpty());
    }

    @Test
    void getAllPermissions_ShouldReturnAllPermissions() {
        // Arrange
        Permission permission1 = new Permission("PERM01", "EMPLOYEE", "READ", null);
        Permission permission2 = new Permission("PERM02", "EMPLOYEE", "WRITE", null);

        when(permissionRepository.findAll()).thenReturn(List.of(permission1, permission2));

        // Act
        List<PermissionDTO> permissions = rolePermissionService.getAllPermissions();

        // Assert
        assertEquals(2, permissions.size());
        assertEquals("READ EMPLOYEE", permissions.get(0).getName());
        assertEquals("WRITE EMPLOYEE", permissions.get(1).getName());
    }
}
