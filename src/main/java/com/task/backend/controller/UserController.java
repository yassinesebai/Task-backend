package com.task.backend.controller;

import com.task.backend.dto.request.AuthRequest;
import com.task.backend.dto.request.UserDTO;
import com.task.backend.dto.request.UserRoleDTO;
import com.task.backend.dto.response.GlobalResponse;
import com.task.backend.dto.response.JwtResponse;
import com.task.backend.service.UserService;
import com.task.backend.utils.GlobalVars;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public GlobalResponse<JwtResponse> login(@RequestBody @Valid AuthRequest authRequest) {
        return userService.authenticate(authRequest);
    }

    @PostMapping("/role/add")
    public GlobalResponse<String> addRole(@RequestBody UserRoleDTO roleDTO) {
        try {
            userService.addRole(roleDTO);
            return new GlobalResponse<>(GlobalVars.OK, "Role added successfully");
        } catch (Exception e) {
            return new GlobalResponse<>(GlobalVars.SERVER_ERROR, "Failed to add role: " + e.getMessage());
        }
    }

    @PostMapping("/create")
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
