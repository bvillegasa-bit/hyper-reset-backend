package com.hyperreset.api.service;

import com.hyperreset.api.dto.request.CitaRequest;
import com.hyperreset.api.dto.response.CitaResponse;
import com.hyperreset.api.entity.Cita;
import com.hyperreset.api.entity.Coach;
import com.hyperreset.api.entity.Deportista;
import com.hyperreset.api.entity.enums.EstadoCita;
import com.hyperreset.api.entity.enums.Rol;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.CitaRepository;
import com.hyperreset.api.repository.CoachRepository;
import com.hyperreset.api.repository.DeportistaRepository;
import com.hyperreset.api.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaService.class);

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private DeportistaRepository deportistaRepository;

    public List<CitaResponse> getAllCitas() {
        log.debug("Fetching all citas");
        return citaRepository.findAllWithRelations().stream()
                .map(this::toCitaResponse)
                .collect(Collectors.toList());
    }

    public CitaResponse getCitaById(Long id) {
        log.debug("Fetching cita by id: {}", id);
        Cita cita = citaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));
        return toCitaResponse(cita);
    }

    public List<CitaResponse> getCitasByCoach(Long coachId) {
        log.debug("Fetching citas by coach id: {}", coachId);
        return citaRepository.findByCoachId(coachId).stream()
                .map(this::toCitaResponse)
                .collect(Collectors.toList());
    }

    public List<CitaResponse> getCitasByDeportista(Long deportistaId) {
        log.debug("Fetching citas by deportista id: {}", deportistaId);
        return citaRepository.findByDeportistaId(deportistaId).stream()
                .map(this::toCitaResponse)
                .collect(Collectors.toList());
    }

    public List<CitaResponse> getCitasByDateRange(LocalDate start, LocalDate end) {
        log.debug("Fetching citas between {} and {}", start, end);
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        return citaRepository.findByFechaHoraBetweenWithRelations(startDateTime, endDateTime).stream()
                .map(this::toCitaResponse)
                .collect(Collectors.toList());
    }

    /**
     * Overloaded method that filters by deportista ID when the authenticated user is DEPORTISTA.
     * For COACH/ADMIN, returns all citas in the date range (same as original).
     */
    public List<CitaResponse> getCitasByDateRange(LocalDate start, LocalDate end, Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isDeportista = false;

        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            isDeportista = "DEPORTISTA".equals(userPrincipal.getRole());
        }

        if (isDeportista) {
            log.debug("DEPORTISTA fetching citas by date range, filtered by usuarioId: {}", userId);
            // For DEPORTISTA, find their deportista record first, then filter citas
            var deportistaOpt = deportistaRepository.findByUsuarioId(userId);
            if (deportistaOpt.isPresent()) {
                Long deportistaId = deportistaOpt.get().getIdDeportista();
                LocalDateTime startDateTime = start.atStartOfDay();
                LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
                return citaRepository.findByDeportistaIdAndFechaHoraBetween(deportistaId, startDateTime, endDateTime).stream()
                        .map(this::toCitaResponse)
                        .collect(Collectors.toList());
            } else {
                log.warn("DEPORTISTA with userId {} not found in deportista table", userId);
                return List.of();
            }
        }

        // COACH/ADMIN: return all citas in range
        return getCitasByDateRange(start, end);
    }

    public CitaResponse createCita(CitaRequest request, Long authenticatedUserId) {
        log.debug("Creating cita for coachId: {}, deportistaId: {} by authenticatedUserId: {}",
                request.getCoachId(), request.getDeportistaId(), authenticatedUserId);

        // Look up coach by usuario.idUsuario (both COACH and DEPORTISTA send usuarioId as coachId)
        Coach coach = coachRepository.findByUsuario_IdUsuario(request.getCoachId())
                .orElseThrow(() -> new ResourceNotFoundException("Coach", "usuarioId", request.getCoachId()));

        Deportista deportista = deportistaRepository.findByIdWithUser(request.getDeportistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Deportista", "id", request.getDeportistaId()));

        // Validate the authenticated user has the right to create this cita
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            if ("COACH".equals(userPrincipal.getRole())) {
                // COACH must be creating a cita for themselves
                if (!request.getCoachId().equals(authenticatedUserId)) {
                    throw new com.hyperreset.api.exception.UnauthorizedException(
                            "No puedes crear citas para otro coach");
                }
            } else if ("DEPORTISTA".equals(userPrincipal.getRole())) {
                // DEPORTISTA must be creating a cita for themselves
                var deportistaOpt = deportistaRepository.findByUsuarioId(userPrincipal.getUserId());
                if (deportistaOpt.isEmpty() || !deportistaOpt.get().getIdDeportista().equals(request.getDeportistaId())) {
                    throw new com.hyperreset.api.exception.UnauthorizedException(
                            "No puedes crear citas para otro deportista");
                }
            }
        }

        LocalDateTime fechaHora = LocalDateTime.of(
                request.getFechaCita(),
                LocalTime.parse(request.getHoraCita(), DateTimeFormatter.ofPattern("HH:mm"))
        );

        Cita cita = new Cita(deportista, coach, fechaHora, request.getMotivo() != null ? request.getMotivo() : "General");
        cita.setNotas(request.getNotas());

        Cita saved = citaRepository.save(cita);
        log.info("Created cita with id: {}", saved.getIdCita());

        // Re-fetch with relations for response
        return getCitaById(saved.getIdCita());
    }

    public CitaResponse updateCita(Long id, CitaRequest request) {
        log.debug("Updating cita with id: {}", id);

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));

        Coach coach = coachRepository.findById(request.getCoachId())
                .orElseThrow(() -> new ResourceNotFoundException("Coach", "id", request.getCoachId()));

        Deportista deportista = deportistaRepository.findByIdWithUser(request.getDeportistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Deportista", "id", request.getDeportistaId()));

        LocalDateTime fechaHora = LocalDateTime.of(
                request.getFechaCita(),
                LocalTime.parse(request.getHoraCita(), DateTimeFormatter.ofPattern("HH:mm"))
        );

        cita.setCoach(coach);
        cita.setDeportista(deportista);
        cita.setFechaHora(fechaHora);
        cita.setTipoSesion(request.getMotivo() != null ? request.getMotivo() : "General");
        cita.setNotas(request.getNotas());

        citaRepository.save(cita);
        log.info("Updated cita with id: {}", id);

        return getCitaById(id);
    }

    public void deleteCita(Long id) {
        log.debug("Deleting cita with id: {}", id);
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));
        citaRepository.delete(cita);
        log.info("Deleted cita with id: {}", id);
    }

    public CitaResponse updateCitaEstado(Long id, EstadoCita estado) {
        log.debug("Updating estado for cita id: {} to {}", id, estado);
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));
        cita.setEstado(estado);
        citaRepository.save(cita);
        log.info("Updated estado for cita id: {} to {}", id, estado);
        return getCitaById(id);
    }

    private CitaResponse toCitaResponse(Cita cita) {
        CitaResponse response = new CitaResponse();
        response.setId(cita.getIdCita());

        String coachNombre = cita.getCoach().getUsuario().getNombres()
                + " " + cita.getCoach().getUsuario().getApellidos();
        response.setCoachNombre(coachNombre);

        String deportistaNombre = cita.getDeportista().getUsuario().getNombres()
                + " " + cita.getDeportista().getUsuario().getApellidos();
        response.setDeportistaNombre(deportistaNombre);

        response.setFechaCita(cita.getFechaHora().toLocalDate());
        response.setHoraCita(cita.getFechaHora().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        response.setEstado(cita.getEstado().name());
        response.setMotivo(cita.getTipoSesion());
        response.setNotas(cita.getNotas());

        return response;
    }
}
