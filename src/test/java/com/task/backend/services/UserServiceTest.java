package com.task.backend.services;

import com.task.backend.dto.request.AuthRequest;
import com.task.backend.dto.request.UserDTO;
import com.task.backend.dto.response.UserResponseDTO;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserManagementRepository userManagementRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void authenticate_ShouldThrowException_WhenAuthenticationFails() {
        String username = "invalidUser";
        String password = "invalidPassword";
        AuthRequest authRequest = new AuthRequest(username, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UsernameNotFoundException("Invalid user request!"));

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.authenticate(authRequest)
        );
        assertEquals("Invalid user request!", exception.getMessage());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserResponseDTOs() {
        // Mock data
        UserManagement user1 = new UserManagement();
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");
        user1.setRole(new UserRole("1", "ROLE_USER"));

        UserManagement user2 = new UserManagement();
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setRole(new UserRole("2", "ROLE_ADMIN"));

        List<UserManagement> mockUsers = List.of(user1, user2);

        UserResponseDTO dto1 = new UserResponseDTO();
        dto1.setFirstName("John");
        dto1.setLastName("Doe");
        dto1.setEmail("john.doe@example.com");
        dto1.setRoleId("1");
        dto1.setRoleName("ROLE_USER");

        UserResponseDTO dto2 = new UserResponseDTO();
        dto2.setFirstName("Jane");
        dto2.setLastName("Smith");
        dto2.setEmail("jane.smith@example.com");
        dto2.setRoleId("2");
        dto2.setRoleName("ROLE_ADMIN");

        when(userManagementRepository.findAll()).thenReturn(mockUsers);

        when(modelMapper.map(user1, UserResponseDTO.class)).thenReturn(dto1);
        when(modelMapper.map(user2, UserResponseDTO.class)).thenReturn(dto2);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
        assertEquals("john.doe@example.com", result.get(0).getEmail());
        assertEquals("1", result.get(0).getRoleId());
        assertEquals("ROLE_USER", result.get(0).getRoleName());

        assertEquals("Jane", result.get(1).getFirstName());
        assertEquals("Smith", result.get(1).getLastName());
        assertEquals("jane.smith@example.com", result.get(1).getEmail());
        assertEquals("2", result.get(1).getRoleId());
        assertEquals("ROLE_ADMIN", result.get(1).getRoleName());

        // Verify interactions
        verify(userManagementRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(user1, UserResponseDTO.class);
        verify(modelMapper, times(1)).map(user2, UserResponseDTO.class);
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyInUse() {
        // Mock data
        UserDTO userDTO = new UserDTO("John", "Doe", "john.doe@example.com", "password123", "1", "IT");
        UserManagement existingUser = new UserManagement();
        existingUser.setEmail("john.doe@example.com");

        // Mock repository behavior
        when(userManagementRepository.findById(userDTO.getEmail())).thenReturn(Optional.of(existingUser));

        // Assertions
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDTO));
        assertEquals("Email address already in use!", exception.getMessage());

        // Verify interactions
        verify(userManagementRepository, times(1)).findById(userDTO.getEmail());
        verify(userRoleRepository, never()).findById(anyString());
        verify(userManagementRepository, never()).save(any(UserManagement.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenRoleNotFound() {
        // Mock data
        UserDTO userDTO = new UserDTO("John", "Doe", "john.doe@example.com", "password123", "invalidRoleId", "IT");

        // Mock repository behavior
        when(userManagementRepository.findById(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRoleRepository.findById(userDTO.getRoleId())).thenReturn(Optional.empty());

        // Assertions
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDTO));
        assertEquals("Role not found", exception.getMessage());

        // Verify interactions
        verify(userManagementRepository, times(1)).findById(userDTO.getEmail());
        verify(userRoleRepository, times(1)).findById(userDTO.getRoleId());
        verify(userManagementRepository, never()).save(any(UserManagement.class));
    }

    @Test
    void createUser_ShouldSaveUserSuccessfully_WhenValidDataProvided() {
        UserDTO userDTO = new UserDTO("John", "Doe", "john.doe@example.com", "password123", "1", "IT");
        UserRole role = new UserRole("1", "ROLE_USER");

        UserManagement mappedUser = new UserManagement();
        mappedUser.setFirstName(userDTO.getFirstName());
        mappedUser.setLastName(userDTO.getLastName());
        mappedUser.setEmail(userDTO.getEmail());
        mappedUser.setDepartment(userDTO.getDepartment());
        mappedUser.setPassword("encodedPassword");
        mappedUser.setRole(role);

        when(userManagementRepository.findById(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRoleRepository.findById(userDTO.getRoleId())).thenReturn(Optional.of(role));
        when(modelMapper.map(userDTO, UserManagement.class)).thenReturn(mappedUser);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");

        userService.createUser(userDTO);

        verify(userManagementRepository, times(1)).findById(userDTO.getEmail());
        verify(userRoleRepository, times(1)).findById(userDTO.getRoleId());
        verify(modelMapper, times(1)).map(userDTO, UserManagement.class);
        verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
        verify(userManagementRepository, times(1)).save(mappedUser);
    }
}
