package com.hyperreset.api.service;

import com.hyperreset.api.auth.JwtTokenProvider;
import com.hyperreset.api.dto.request.ChangePasswordRequest;
import com.hyperreset.api.dto.request.LoginRequest;
import com.hyperreset.api.dto.request.ProfileUpdateRequest;
import com.hyperreset.api.dto.request.RegisterRequest;
import com.hyperreset.api.dto.response.AuthResponse;
import com.hyperreset.api.dto.response.UsuarioResponse;
import com.hyperreset.api.entity.Coach;
import com.hyperreset.api.entity.Deportista;
import com.hyperreset.api.entity.Usuario;
import com.hyperreset.api.entity.enums.Rol;
import com.hyperreset.api.exception.BadRequestException;
import com.hyperreset.api.exception.DuplicateResourceException;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.CoachRepository;
import com.hyperreset.api.repository.DeportistaRepository;
import com.hyperreset.api.repository.UsuarioRepository;
import com.hyperreset.api.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private DeportistaRepository deportistaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        log.debug("Registering new user with email: {}", request.getEmail());

        if (usuarioRepository.existsByCorreo(request.getEmail())) {
            throw new BadRequestException("El correo ya está registrado");
        }

        // Split nombre into nombres and apellidos (first space separates them)
        String nombreCompleto = request.getNombre();
        String[] parts = nombreCompleto.split(" ", 2);
        String nombres = parts[0];
        String apellidos = parts.length > 1 ? parts[1] : "";

        Usuario usuario = new Usuario();
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(request.getEmail());
        usuario.setContrasenaHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario = usuarioRepository.save(usuario);

        // Create role-specific profile
        if (request.getRol() == Rol.COACH) {
            Coach coach = new Coach(usuario);
            coachRepository.save(coach);
            log.debug("Created Coach profile for user ID: {}", usuario.getIdUsuario());
        } else if (request.getRol() == Rol.DEPORTISTA) {
            Deportista deportista = new Deportista(usuario);
            deportistaRepository.save(deportista);
            log.debug("Created Deportista profile for user ID: {}", usuario.getIdUsuario());
        }

        // Generate JWT token
        UserPrincipal userPrincipal = UserPrincipal.create(usuario);
        String token = jwtTokenProvider.generateToken(userPrincipal);

        String nombre = nombres + (apellidos.isEmpty() ? "" : " " + apellidos);

        // Buscar deportistaId si el usuario es DEPORTISTA
        Long deportistaId = null;
        if (usuario.getRol() == Rol.DEPORTISTA) {
            java.util.Optional<com.hyperreset.api.entity.Deportista> depOpt =
                    deportistaRepository.findByUsuarioId(usuario.getIdUsuario());
            if (depOpt.isPresent()) {
                deportistaId = depOpt.get().getIdDeportista();
            }
        }

        return new AuthResponse(token, usuario.getIdUsuario(), usuario.getCorreo(),
                usuario.getRol().name(), nombre, deportistaId);
    }

    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt for email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Load full user to get names
        Usuario usuario = usuarioRepository.findByCorreo(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        String token = jwtTokenProvider.generateToken(userPrincipal);
        String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();

        // Buscar deportistaId si el usuario es DEPORTISTA
        Long deportistaId = null;
        if (usuario.getRol() == Rol.DEPORTISTA) {
            java.util.Optional<com.hyperreset.api.entity.Deportista> depOpt =
                    deportistaRepository.findByUsuarioId(usuario.getIdUsuario());
            if (depOpt.isPresent()) {
                deportistaId = depOpt.get().getIdDeportista();
            }
        }

        return new AuthResponse(token, usuario.getIdUsuario(), usuario.getCorreo(),
                usuario.getRol().name(), nombreCompleto, deportistaId);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse getProfile(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));

        String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();

        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getRol().name(),
                nombreCompleto,
                usuario.getApellidos(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.getFechaNacimiento(),
                usuario.getFechaRegistro().toLocalDate(),
                usuario.getActivo()
        );
    }

    public UsuarioResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        log.debug("Updating profile for userId: {}", userId);

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));

        // Update only non-null fields
        if (request.getNombres() != null) {
            usuario.setNombres(request.getNombres());
        }
        if (request.getApellidos() != null) {
            usuario.setApellidos(request.getApellidos());
        }
        if (request.getCorreo() != null && !request.getCorreo().isEmpty()) {
            // Check if another user already has this email
            usuarioRepository.findByCorreo(request.getCorreo()).ifPresent(existing -> {
                if (!existing.getIdUsuario().equals(userId)) {
                    throw new DuplicateResourceException("El correo ya está registrado");
                }
            });
            usuario.setCorreo(request.getCorreo());
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getDireccion() != null) {
            usuario.setDireccion(request.getDireccion());
        }
        if (request.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(request.getFechaNacimiento());
        }

        usuario = usuarioRepository.save(usuario);

        String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();

        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getRol().name(),
                nombreCompleto,
                usuario.getApellidos(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.getFechaNacimiento(),
                usuario.getFechaRegistro().toLocalDate(),
                usuario.getActivo()
        );
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.debug("Changing password for userId: {}", userId);

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));

        // Validate current password matches stored hash
        if (!passwordEncoder.matches(request.getCurrentPassword(), usuario.getContrasenaHash())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Validate new password matches confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        // Encode and save new password
        usuario.setContrasenaHash(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);

        log.info("Password changed successfully for userId: {}", userId);
    }
}
