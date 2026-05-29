package com.hyperreset.api.service;

import com.hyperreset.api.auth.JwtTokenProvider;
import com.hyperreset.api.dto.request.LoginRequest;
import com.hyperreset.api.dto.request.RegisterRequest;
import com.hyperreset.api.dto.response.AuthResponse;
import com.hyperreset.api.dto.response.UsuarioResponse;
import com.hyperreset.api.entity.Coach;
import com.hyperreset.api.entity.Deportista;
import com.hyperreset.api.entity.Usuario;
import com.hyperreset.api.entity.enums.Rol;
import com.hyperreset.api.exception.BadRequestException;
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

        return new AuthResponse(token, usuario.getIdUsuario(), usuario.getCorreo(),
                usuario.getRol().name(), nombre);
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

        return new AuthResponse(token, usuario.getIdUsuario(), usuario.getCorreo(),
                usuario.getRol().name(), nombreCompleto);
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
                usuario.getFechaRegistro().toLocalDate(),
                usuario.getActivo()
        );
    }
}
