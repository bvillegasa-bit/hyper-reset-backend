package com.hyperreset.api.service;

import com.hyperreset.api.dto.request.DeportistaRequest;
import com.hyperreset.api.dto.response.CoachResponse;
import com.hyperreset.api.dto.response.DeportistaResponse;
import com.hyperreset.api.entity.Coach;
import com.hyperreset.api.entity.Deportista;
import com.hyperreset.api.entity.Usuario;
import com.hyperreset.api.entity.enums.Rol;
import com.hyperreset.api.exception.BadRequestException;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.CoachRepository;
import com.hyperreset.api.repository.DeportistaRepository;
import com.hyperreset.api.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeportistaService {

    private static final Logger log = LoggerFactory.getLogger(DeportistaService.class);

    @Autowired
    private DeportistaRepository deportistaRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<DeportistaResponse> getAllDeportistas() {
        List<Deportista> deportistas = deportistaRepository.findAll();
        return deportistas.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeportistaResponse getDeportistaById(Long id) {
        Deportista deportista = deportistaRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deportista", "id", id));
        return mapToResponse(deportista);
    }

    @Transactional(readOnly = true)
    public List<DeportistaResponse> getDeportistasByCoach(Long coachId) {
        // Verify coach exists
        coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach", "id", coachId));

        List<Deportista> deportistas = deportistaRepository.findByCoachIdWithUser(coachId);
        return deportistas.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DeportistaResponse createDeportista(DeportistaRequest request) {
        log.debug("Creating new Deportista with nombres: {}", request.getNombres());

        // Check if email already exists
        if (usuarioRepository.existsByCorreo(request.getEmail())) {
            throw new BadRequestException("El correo ya está registrado");
        }

        // Validate coach exists
        Coach coach = null;
        if (request.getCoachId() != null) {
            coach = coachRepository.findById(request.getCoachId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coach", "id", request.getCoachId()));
        }

        // Generate random temporary password
        String tempPassword = generateRandomPassword();

        // Create Usuario for the Deportista
        Usuario usuario = new Usuario();
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setCorreo(request.getEmail());
        usuario.setContrasenaHash(passwordEncoder.encode(tempPassword));
        usuario.setRol(Rol.DEPORTISTA);
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario = usuarioRepository.save(usuario);

        // Create Deportista profile
        Deportista deportista = new Deportista(usuario);
        deportista.setCoach(coach);
        deportista = deportistaRepository.save(deportista);

        log.debug("Deportista created with ID: {}", deportista.getIdDeportista());

        DeportistaResponse response = mapToResponse(deportista);
        response.setTempPassword(tempPassword);
        return response;
    }

    public DeportistaResponse updateDeportista(Long id, DeportistaRequest request) {
        log.debug("Updating Deportista with ID: {}", id);

        Deportista deportista = deportistaRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deportista", "id", id));

        // Update coach if provided
        if (request.getCoachId() != null) {
            Coach coach = coachRepository.findById(request.getCoachId())
                    .orElseThrow(() -> new ResourceNotFoundException("Coach", "id", request.getCoachId()));
            deportista.setCoach(coach);
        }

        // Update Usuario fields
        Usuario usuario = deportista.getUsuario();
        if (request.getNombres() != null) {
            usuario.setNombres(request.getNombres());
        }
        if (request.getApellidos() != null) {
            usuario.setApellidos(request.getApellidos());
        }
        if (request.getEmail() != null) {
            // Check new email doesn't conflict with another user
            if (!request.getEmail().equals(usuario.getCorreo())
                    && usuarioRepository.existsByCorreo(request.getEmail())) {
                throw new BadRequestException("El correo ya está registrado por otro usuario");
            }
            usuario.setCorreo(request.getEmail());
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
        usuarioRepository.save(usuario);

        deportista = deportistaRepository.save(deportista);
        return mapToResponse(deportista);
    }

    public void deleteDeportista(Long id) {
        log.debug("Deleting Deportista with ID: {}", id);

        Deportista deportista = deportistaRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deportista", "id", id));

        // Delete the associated Usuario as well
        Usuario usuario = deportista.getUsuario();
        deportistaRepository.delete(deportista);
        usuarioRepository.delete(usuario);

        log.debug("Deportista {} deleted", id);
    }

    private DeportistaResponse mapToResponse(Deportista deportista) {
        Usuario usuario = deportista.getUsuario();

        String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();

        String coachNombre = null;
        if (deportista.getCoach() != null && deportista.getCoach().getUsuario() != null) {
            Usuario coachUser = deportista.getCoach().getUsuario();
            coachNombre = coachUser.getNombres() + " " + coachUser.getApellidos();
        }

        return new DeportistaResponse(
                deportista.getIdDeportista(),
                deportista.getUsuario().getIdUsuario(),
                nombreCompleto,
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getFechaNacimiento(),
                coachNombre,
                usuario.getFechaRegistro().toLocalDate(),
                usuario.getDireccion()
        );
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public List<CoachResponse> getAvailableCoaches() {
        List<Coach> coaches = coachRepository.findAll();
        return coaches.stream()
                .map(this::mapCoachToResponse)
                .collect(Collectors.toList());
    }

    private CoachResponse mapCoachToResponse(Coach coach) {
        Usuario usuario = coach.getUsuario();
        String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();

        CoachResponse response = new CoachResponse(
                coach.getIdCoach(),
                nombreCompleto,
                usuario.getCorreo(),
                coach.getEspecialidad(),
                coach.getDescripcion(),
                usuario.getTelefono(),
                usuario.getFechaRegistro().toLocalDate()
        );
        response.setUsuarioId(usuario.getIdUsuario());
        return response;
    }

    @Transactional(readOnly = true)
    public CoachResponse getMiCoach(Long usuarioId) {
        Deportista deportista = deportistaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Deportista", "usuarioId", usuarioId));

        Coach coach = deportista.getCoach();
        if (coach == null) {
            throw new ResourceNotFoundException("Coach", "deportistaId", deportista.getIdDeportista());
        }

        return mapCoachToResponse(coach);
    }
}
