package com.task.backend.controller;

import com.task.backend.dto.request.AuthRequest;
import com.task.backend.dto.request.UserDTO;
import com.task.backend.dto.response.GlobalResponse;
import com.task.backend.dto.response.JwtResponse;
import com.task.backend.dto.response.UserResponseDTO;
import com.task.backend.service.UserService;
import com.task.backend.utils.GlobalVars;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public GlobalResponse<JwtResponse> login(@RequestBody @Valid AuthRequest authRequest) {
        return userService.authenticate(authRequest);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/all")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'USER', 'READ')")
    public GlobalResponse<List<UserResponseDTO>> getUsers() {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            return new GlobalResponse<>(GlobalVars.OK, "Users retrieved successfully", users);
        } catch (Exception e) {
            return new GlobalResponse<>(GlobalVars.SERVER_ERROR, "Failed to fetch users: " + e.getMessage());
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/create")
    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication, 'USER', 'CREATE')")
    public GlobalResponse<String> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            result.getAllErrors().forEach(error -> errorMsg.append(error.getDefaultMessage()).append("; "));
            return new GlobalResponse<>(GlobalVars.INVALID_ACTION, "Validation failed: " + errorMsg);
        }
        try {
            userService.createUser(userDTO);
            return new GlobalResponse<>(GlobalVars.OK, "User created successfully");
        } catch (Exception e) {
            return new GlobalResponse<>(GlobalVars.SERVER_ERROR, "Failed to create user: " + e.getMessage());
        }
    }
}
