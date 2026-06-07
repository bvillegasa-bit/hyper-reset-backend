package com.hyperreset.api.service;

import com.hyperreset.api.auth.JwtTokenProvider;
import com.hyperreset.api.dto.request.ChangePasswordRequest;
import com.hyperreset.api.dto.request.ProfileUpdateRequest;
import com.hyperreset.api.dto.response.UsuarioResponse;
import com.hyperreset.api.entity.Usuario;
import com.hyperreset.api.entity.enums.Rol;
import com.hyperreset.api.exception.BadRequestException;
import com.hyperreset.api.exception.DuplicateResourceException;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.CoachRepository;
import com.hyperreset.api.repository.DeportistaRepository;
import com.hyperreset.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService — updateProfile and changePassword operations.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CoachRepository coachRepository;

    @Mock
    private DeportistaRepository deportistaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private Usuario existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new Usuario();
        existingUser.setIdUsuario(1L);
        existingUser.setNombres("Juan");
        existingUser.setApellidos("Pérez");
        existingUser.setCorreo("juan@example.com");
        existingUser.setContrasenaHash("encoded_old_password");
        existingUser.setRol(Rol.COACH);
        existingUser.setActivo(true);
        existingUser.setFechaRegistro(LocalDateTime.now());
        existingUser.setTelefono("123456789");
        existingUser.setDireccion("Calle 123");
        existingUser.setFechaNacimiento(LocalDate.of(1990, 1, 15));
    }

    // ==================================================================
    // updateProfile tests
    // ==================================================================

    @Test
    void updateProfile_WithAllFields_Success() {
        // Arrange
        Long userId = 1L;
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "Carlos", "López", "carlos@example.com",
                "987654321", "Av. Nueva 456", LocalDate.of(1988, 5, 20)
        );

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.findByCorreo("carlos@example.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UsuarioResponse response = authService.updateProfile(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals("Carlos", existingUser.getNombres());
        assertEquals("López", existingUser.getApellidos());
        assertEquals("carlos@example.com", existingUser.getCorreo());
        assertEquals("987654321", existingUser.getTelefono());
        assertEquals("Av. Nueva 456", existingUser.getDireccion());
        assertEquals(LocalDate.of(1988, 5, 20), existingUser.getFechaNacimiento());
        verify(usuarioRepository).save(existingUser);
    }

    @Test
    void updateProfile_WithNullOptionalFields_SkipsNulls() {
        // Arrange
        Long userId = 1L;
        // Only provide nombres and apellidos — all others are null
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "Carlos", "López", null,
                null, null, null
        );

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UsuarioResponse response = authService.updateProfile(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals("Carlos", existingUser.getNombres());
        assertEquals("López", existingUser.getApellidos());
        // Fields not in the request should remain unchanged
        assertEquals("juan@example.com", existingUser.getCorreo());
        assertEquals("123456789", existingUser.getTelefono());
        assertEquals("Calle 123", existingUser.getDireccion());
        assertEquals(LocalDate.of(1990, 1, 15), existingUser.getFechaNacimiento());
        verify(usuarioRepository).save(existingUser);
    }

    @Test
    void updateProfile_WithCorreoTakenByAnotherUser_ThrowsDuplicateResourceException() {
        // Arrange
        Long userId = 1L;
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "Carlos", "López", "otro@example.com",
                null, null, null
        );

        Usuario anotherUser = new Usuario();
        anotherUser.setIdUsuario(2L);
        anotherUser.setCorreo("otro@example.com");

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.findByCorreo("otro@example.com")).thenReturn(Optional.of(anotherUser));

        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> authService.updateProfile(userId, request));
        assertEquals("El correo ya está registrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void updateProfile_WithSameCorreo_AllowsUpdate() {
        // Arrange
        Long userId = 1L;
        // User is updating to the same email they already have
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "Carlos", "López", "juan@example.com",
                null, null, null
        );

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.findByCorreo("juan@example.com")).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UsuarioResponse response = authService.updateProfile(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals("juan@example.com", existingUser.getCorreo());
        verify(usuarioRepository).save(existingUser);
    }

    @Test
    void updateProfile_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        Long userId = 999L;
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "Test", "User", null, null, null, null
        );

        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> authService.updateProfile(userId, request));
    }

    // ==================================================================
    // changePassword tests
    // ==================================================================

    @Test
    void changePassword_Success() {
        // Arrange
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest(
                "oldPassword", "newPassword123", "newPassword123"
        );

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("oldPassword", "encoded_old_password")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encoded_new_password");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        authService.changePassword(userId, request);

        // Assert
        assertEquals("encoded_new_password", existingUser.getContrasenaHash());
        verify(passwordEncoder).encode("newPassword123");
        verify(usuarioRepository).save(existingUser);
    }

    @Test
    void changePassword_WrongCurrentPassword_ThrowsBadRequestException() {
        // Arrange
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest(
                "wrongPassword", "newPassword123", "newPassword123"
        );

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongPassword", "encoded_old_password")).thenReturn(false);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.changePassword(userId, request));
        assertEquals("Current password is incorrect", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void changePassword_MismatchedNewPasswords_ThrowsBadRequestException() {
        // Arrange
        Long userId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest(
                "oldPassword", "newPassword123", "differentConfirmPassword"
        );

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("oldPassword", "encoded_old_password")).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.changePassword(userId, request));
        assertEquals("New password and confirm password do not match", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }
}
