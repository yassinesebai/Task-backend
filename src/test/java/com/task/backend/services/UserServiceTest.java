package com.task.backend.services;

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
import com.task.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService; // << Class tested

    @Mock
    private UserManagementRepository userManagementRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("yassine@gmail.com");

        when(userManagementRepository.findById(userDTO.getEmail()))
                .thenReturn(Optional.of(new UserManagement()));

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
        userDTO.setDepartment("IT");

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

        userService.createUser(userDTO);

        verify(userManagementRepository, times(1)).save(user);
        verify(userRoleRepository, times(1)).findById(userDTO.getRoleId());
        verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
    }

    @Test
    void authenticate_shouldReturnToken_whenCredentialsAreValid() {
        AuthRequest authRequest = new AuthRequest("test@example.com", "password123");
        UserManagement userManagement = new UserManagement();
        userManagement.setEmail("test@example.com");
        userManagement.setDepartment("IT");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userManagementRepository.findById(authRequest.getUsername()))
                .thenReturn(Optional.of(userManagement));
        when(jwtService.GenerateToken(authRequest.getUsername())).thenReturn("jwtToken");

        GlobalResponse<JwtResponse> response = userService.authenticate(authRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getData().getAccessToken());
        assertEquals("IT", response.getData().getDepartment());
    }

    @Test
    void authenticate_shouldThrowException_whenCredentialsAreInvalid() {
        AuthRequest authRequest = new AuthRequest("invalid@example.com", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UsernameNotFoundException("Invalid user request!"));

        assertThrows(UsernameNotFoundException.class, () -> userService.authenticate(authRequest));
    }

    @Test
    void getAllUsers_shouldReturnUserList_whenUsersExist() {
        // Arrange
        UserManagement user1 = new UserManagement();
        user1.setEmail("user1@example.com");
        UserManagement user2 = new UserManagement();
        user2.setEmail("user2@example.com");
        List<UserManagement> users = Arrays.asList(user1, user2);

        when(userManagementRepository.findAll()).thenReturn(users);
        when(modelMapper.map(user1, UserResponseDTO.class)).thenReturn(new UserResponseDTO());
        when(modelMapper.map(user2, UserResponseDTO.class)).thenReturn(new UserResponseDTO());

        List<UserResponseDTO> response = userService.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
    }
}

