package com.hyperreset.api.controller;

import com.hyperreset.api.dto.request.ChangePasswordRequest;
import com.hyperreset.api.dto.request.LoginRequest;
import com.hyperreset.api.dto.request.ProfileUpdateRequest;
import com.hyperreset.api.dto.request.RegisterRequest;
import com.hyperreset.api.dto.response.ApiResponse;
import com.hyperreset.api.dto.response.AuthResponse;
import com.hyperreset.api.dto.response.UsuarioResponse;
import com.hyperreset.api.security.CurrentUser;
import com.hyperreset.api.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.debug("POST /api/auth/register - email: {}", request.getEmail());
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registro exitoso", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.debug("POST /api/auth/login - email: {}", request.getEmail());
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Inicio de sesión exitoso", authResponse));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UsuarioResponse>> getProfile(@CurrentUser Long userId) {
        log.debug("GET /api/auth/profile - userId: {}", userId);
        UsuarioResponse usuarioResponse = authService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Perfil obtenido", usuarioResponse));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UsuarioResponse>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            @CurrentUser Long userId) {
        log.debug("PUT /api/auth/profile - userId: {}", userId);
        UsuarioResponse response = authService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Perfil actualizado exitosamente", response));
    }

    @PatchMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @CurrentUser Long userId) {
        log.debug("PATCH /api/auth/change-password - userId: {}", userId);
        authService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}
