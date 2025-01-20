package com.task.backend.service;

import com.task.backend.dto.request.AuthRequest;
import com.task.backend.dto.request.UserDTO;
import com.task.backend.dto.response.GlobalResponse;
import com.task.backend.dto.response.JwtResponse;
import com.task.backend.dto.response.UserResponseDTO;
import com.task.backend.model.UserManagement;
import com.task.backend.model.UserRole;
import com.task.backend.repository.UserManagementRepository;
import com.task.backend.repository.UserRoleRepository;
import com.task.backend.security.JwtService;
import com.task.backend.utils.GlobalVars;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
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
            String department = "";
            Optional<UserManagement> userManagement = userManagementRepository
                    .findById(authRequest.getUsername());
            if (userManagement.isPresent()) {
                department = userManagement.get().getDepartment();
            }
            String token = jwtService.GenerateToken(trimmedUsername);
            logger.info("Token successfully generated");

            // retrieve role from auth object
            String role = authentication.getAuthorities()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No role found"))
                    .getAuthority();

            UserRole userRole = userRoleRepository.findByName(role);
            List<String> permissions;
            if (userRole != null) {
                permissions = userRole.getRolePermissions()
                        .stream()
                        .map(rolePermission -> rolePermission.getPermission().getAction() + " " + rolePermission.getPermission().getTableName())
                        .toList();
            } else {
                throw new AccessDeniedException("Invalid user role!");
            }
            return new GlobalResponse<>(
                    GlobalVars.OK, "user logged in successfully!",
                    JwtResponse.builder()
                            .accessToken(token)
                            .department(department)
                            .role(role)
                            .permissions(permissions)
                            .build()
            );
        } else {
            logger.warn("Authentication failed");
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    public List<UserResponseDTO> getAllUsers() {
        List<UserManagement> users = userManagementRepository.findAll();
        return users.stream()
                .map(user -> {
                    UserResponseDTO response = modelMapper.map(user, UserResponseDTO.class);
                    response.setRoleId(user.getRole().getId());
                    response.setRoleName(user.getRole().getName());
                    return response;
                }).toList();
    }

    @Transactional
    public void createUser(UserDTO userDTO) {
        Optional<UserManagement> userOptional = userManagementRepository.findById(userDTO.getEmail());
        if (userOptional.isPresent()) {
            throw new IllegalArgumentException("Email address already in use!");
        }
        UserRole role = userRoleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        UserManagement user = modelMapper.map(userDTO, UserManagement.class);
        user.setUserCode(user.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(role);

        userManagementRepository.save(user);
    }

}
