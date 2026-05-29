package com.hyperreset.api.service;

import com.hyperreset.api.dto.request.CitaRequest;
import com.hyperreset.api.dto.response.CitaResponse;
import com.hyperreset.api.entity.Cita;
import com.hyperreset.api.entity.Coach;
import com.hyperreset.api.entity.Deportista;
import com.hyperreset.api.entity.enums.EstadoCita;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.CitaRepository;
import com.hyperreset.api.repository.CoachRepository;
import com.hyperreset.api.repository.DeportistaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public CitaResponse createCita(CitaRequest request) {
        log.debug("Creating cita for coachId: {}, deportistaId: {}",
                request.getCoachId(), request.getDeportistaId());

        Coach coach = coachRepository.findById(request.getCoachId())
                .orElseThrow(() -> new ResourceNotFoundException("Coach", "id", request.getCoachId()));

        Deportista deportista = deportistaRepository.findByIdWithUser(request.getDeportistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Deportista", "id", request.getDeportistaId()));

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
