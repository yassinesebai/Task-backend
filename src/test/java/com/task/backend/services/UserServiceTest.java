package com.task.backend.services;

import com.task.backend.dto.request.UserDTO;
import com.task.backend.model.UserManagement;
import com.task.backend.model.UserRole;
import com.task.backend.repository.UserManagementRepository;
import com.task.backend.repository.UserRoleRepository;
import com.task.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService; // Class under test

    @Mock
    private UserManagementRepository userManagementRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");

        when(userManagementRepository.findById(userDTO.getEmail()))
                .thenReturn(Optional.of(new UserManagement()));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(userDTO));
        assertEquals("Email address already in use!", exception.getMessage());

        verify(userManagementRepository, times(1)).findById(userDTO.getEmail());
        verifyNoMoreInteractions(userManagementRepository);
    }

    @Test
    void createUser_shouldSaveUser_whenValidRequest() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");
        userDTO.setRoleId("role123");

        UserRole role = new UserRole();
        role.setId("role123");

        UserManagement user = new UserManagement();
        user.setEmail(userDTO.getEmail());

        when(userManagementRepository.findById(userDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(userRoleRepository.findById(userDTO.getRoleId()))
                .thenReturn(Optional.of(role));
        when(passwordEncoder.encode(userDTO.getPassword()))
                .thenReturn("encryptedPassword");
        when(modelMapper.map(userDTO, UserManagement.class)).thenReturn(user);

        // Act
        userService.createUser(userDTO);

        // Assert
        verify(userManagementRepository, times(1)).save(user);
        verify(userRoleRepository, times(1)).findById(userDTO.getRoleId());
        verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
    }
}

