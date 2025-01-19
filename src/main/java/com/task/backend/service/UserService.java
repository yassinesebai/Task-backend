package com.task.backend.service;

import com.task.backend.dto.request.AuthRequest;
import com.task.backend.dto.request.UserDTO;
import com.task.backend.dto.request.UserRoleDTO;
import com.task.backend.dto.response.GlobalResponse;
import com.task.backend.dto.response.JwtResponse;
import com.task.backend.model.Permission;
import com.task.backend.model.RolePermission;
import com.task.backend.model.UserManagement;
import com.task.backend.model.UserRole;
import com.task.backend.repository.PermissionRepository;
import com.task.backend.repository.UserManagementRepository;
import com.task.backend.repository.UserRoleRepository;
import com.task.backend.security.JwtService;
import com.task.backend.utils.GlobalVars;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final ModelMapper modelMapper;
    private final UserRoleRepository userRoleRepository;
    private final UserManagementRepository userManagementRepository;
    private final PermissionRepository permissionRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public GlobalResponse<JwtResponse> authenticate(AuthRequest authRequest) {
        // Trim username input before authentication
        String trimmedUsername = authRequest.getUsername().trim();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(trimmedUsername, authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            String token = jwtService.GenerateToken(trimmedUsername);
            logger.info("Token successfully generated");

            // retrieve role from auth object
            String role = authentication.getAuthorities()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No role found"))
                    .getAuthority();

            return new GlobalResponse<>(
                    GlobalVars.OK, "user logged in successfully!",
                    JwtResponse.builder()
                    .accessToken(token)
                    .role(role)
                    .build()
            );
        } else {
            logger.warn("Authentication failed");
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @Transactional
    public void addRole(UserRoleDTO roleDTO) {
        // Create a new UserRole
        UserRole userRole = new UserRole();
        userRole.setName(roleDTO.getName());

        // Fetch the permissions and create RolePermission entities
        List<RolePermission> rolePermissions = roleDTO.getPermissionIds().stream()
                .map(permissionId -> {
                    Permission permission = permissionRepository.findById(permissionId)
                            .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionId));
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRole(userRole);
                    rolePermission.setPermission(permission);
                    return rolePermission;
                }).toList();

        // Associate RolePermissions with the UserRole
        userRole.setRolePermissions(rolePermissions);

        // Save the UserRole and cascade the RolePermissions
        userRoleRepository.save(userRole);
    }

    @Transactional
    public void createUser(UserDTO userDTO) {
        Optional<UserManagement> userOptional = userManagementRepository.findById(userDTO.getEmail());
        if (userOptional.isPresent()) {
            throw new IllegalArgumentException("Email address already in use!");
        }
        // Fetch role, create user, and save them in a single transaction
        UserRole role = userRoleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        UserManagement user = modelMapper.map(userDTO, UserManagement.class);
        user.setUserCode(user.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(role);

        userManagementRepository.save(user); // If something goes wrong here, everything gets rolled back.
    }

}
