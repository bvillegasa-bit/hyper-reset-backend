package com.hyperreset.api.service;

import com.hyperreset.api.dto.response.ActividadRecienteItem;
import com.hyperreset.api.dto.response.DashboardActivityResponse;
import com.hyperreset.api.dto.response.DashboardCoachResponse;
import com.hyperreset.api.dto.response.DashboardDeportistaResponse;
import com.hyperreset.api.dto.response.DashboardDeportistaResponse.LogrosInfo;
import com.hyperreset.api.dto.response.DashboardDeportistaResponse.MetaDelMesInfo;
import com.hyperreset.api.dto.response.DashboardDeportistaResponse.ProgresoSemanalItem;
import com.hyperreset.api.dto.response.DashboardDeportistaResponse.ProximaCitaInfo;
import com.hyperreset.api.entity.Cita;
import com.hyperreset.api.entity.Deportista;
import com.hyperreset.api.entity.ResultadoTest;
import com.hyperreset.api.entity.TestFisico;
import com.hyperreset.api.entity.enums.EstadoTest;
import com.hyperreset.api.repository.CitaRepository;
import com.hyperreset.api.repository.DeportistaRepository;
import com.hyperreset.api.repository.ResultadoTestRepository;
import com.hyperreset.api.repository.TestFisicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that aggregates data for the dashboard endpoints.
 * Provides both deportista and coach dashboards.
 */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private DeportistaRepository deportistaRepository;

    @Autowired
    private ResultadoTestRepository resultadoTestRepository;

    @Autowired
    private TestFisicoRepository testFisicoRepository;

    // ==================================================================
    // Deportista Dashboard
    // ==================================================================

    /**
     * Builds the deportista dashboard response.
     */
    public DashboardDeportistaResponse getDashboardDeportista(Long deportistaId) {
        log.debug("Building dashboard for deportistaId: {}", deportistaId);

        DashboardDeportistaResponse response = new DashboardDeportistaResponse();

        // 1. Next appointment
        response.setProximaCita(getProximaCita(deportistaId));

        // 2. Weekly progress (last 7 days of test activity)
        response.setProgresoSemanal(getProgresoSemanal(deportistaId));

        // 3. Monthly goal (placeholder — null if not available)
        response.setMetaDelMes(null); // No meta table exists yet

        // 4. Achievements
        response.setLogros(getLogros(deportistaId));

        return response;
    }

    /**
     * Finds the next future appointment for the deportista.
     */
    private ProximaCitaInfo getProximaCita(Long deportistaId) {
        LocalDateTime now = LocalDateTime.now();
        List<Cita> citas = citaRepository.findByDeportistaIdAndFechaHoraAfter(deportistaId, now);

        if (citas.isEmpty()) {
            return null;
        }

        // Sort by fechaHora ascending and take the first
        citas.sort(Comparator.comparing(Cita::getFechaHora));
        Cita next = citas.get(0);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String coachNombre;
        if (next.getCoach() != null && next.getCoach().getUsuario() != null) {
            coachNombre = next.getCoach().getUsuario().getNombres()
                    + " " + next.getCoach().getUsuario().getApellidos();
        } else {
            coachNombre = "Sin asignar";
        }

        return new ProximaCitaInfo(
                next.getFechaHora().toLocalDate().format(dateFormatter),
                next.getFechaHora().toLocalTime().format(timeFormatter),
                coachNombre,
                next.getTipoSesion()
        );
    }

    /**
     * Calculates weekly progress: for each of the last 7 days, counts the number
     * of test results recorded. Returns 7 items (L M M J V S D).
     */
    private List<ProgresoSemanalItem> getProgresoSemanal(Long deportistaId) {
        LocalDate today = LocalDate.now();
        // Get Monday of current week
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        // Convert LocalDate to LocalDateTime to match t.fechaEjecucion type (LocalDateTime)
        LocalDateTime startDateTime = monday.atStartOfDay();
        LocalDateTime endDateTime = sunday.atTime(LocalTime.MAX);

        List<ResultadoTest> resultados = resultadoTestRepository
                .findByDeportistaIdAndFechaBetween(deportistaId, startDateTime, endDateTime);

        // Group by date and count (filter out records with null testFisico)
        Map<LocalDate, Long> countByDay = resultados.stream()
                .filter(r -> r.getTestFisico() != null && r.getTestFisico().getFechaEjecucion() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getTestFisico().getFechaEjecucion().toLocalDate(),
                        Collectors.counting()
                ));

        // Build 7-day array (L M M J V S D)
        String[] dayLabels = {"L", "M", "M", "J", "V", "S", "D"};
        List<ProgresoSemanalItem> weekly = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            long count = countByDay.getOrDefault(day, 0L);
            // Scale to 0-100 for the chart (max 10 tests per day = 100%)
            int scaledValue = Math.min((int) (count * 10), 100);
            weekly.add(new ProgresoSemanalItem(dayLabels[i], scaledValue));
        }

        return weekly;
    }

    /**
     * Calculates logros (achievements) for the deportista.
     */
    private LogrosInfo getLogros(Long deportistaId) {
        // Count distinct tipoTest values across all resultados
        List<ResultadoTest> resultados = resultadoTestRepository
                .findByTestFisicoDeportistaId(deportistaId);

        Set<String> tiposCompletados = resultados.stream()
                .filter(r -> r.getTipoTest() != null)
                .map(r -> r.getTipoTest().name())
                .collect(Collectors.toSet());

        int testsCompletados = tiposCompletados.size();
        int totalTests = 8; // fixed 8 test types

        // Count distinct test sessions (TestFisico)
        List<TestFisico> tests = testFisicoRepository.findByDeportistaId(deportistaId);
        int sesiones = tests.size();

        // Racha: for now, just a placeholder (0 if no tests)
        // In a real scenario, this would check consecutive days with activity
        int racha = sesiones > 0 ? Math.min(sesiones, 7) : 0;

        return new LogrosInfo(testsCompletados, totalTests, sesiones, racha);
    }

    // ==================================================================
    // Coach Dashboard
    // ==================================================================

    /**
     * Builds the coach dashboard response.
     */
    public DashboardCoachResponse getDashboardCoach(Long coachId) {
        log.debug("Building dashboard for coachId: {}", coachId);

        DashboardCoachResponse response = new DashboardCoachResponse();

        // 1. Stats
        response.setEstadisticas(getEstadisticas(coachId));

        // 2. Recent activity
        response.setActividadReciente(getActividadReciente(coachId));

        // 3. Upcoming appointments
        response.setProximasCitas(getProximasCitasCoach(coachId));

        return response;
    }

    /**
     * Calculates coach statistics: patients today, pending tests, reports.
     */
    private DashboardCoachResponse.EstadisticasInfo getEstadisticas(Long coachId) {
        // Patients with appointments today
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        List<Cita> citasHoy = citaRepository.findByCoachIdAndFechaHoraBetween(coachId, startOfDay, endOfDay);
        long pacientesHoy = citasHoy.stream()
                .map(c -> c.getDeportista().getIdDeportista())
                .distinct()
                .count();

        // Pending tests: count of EN_PROGRESO tests for the coach's patients
        List<Deportista> pacientes = deportistaRepository.findByCoachIdCoach(coachId);
        long pruebasPendientes = 0;
        for (Deportista d : pacientes) {
            List<TestFisico> tests = testFisicoRepository.findByDeportistaId(d.getIdDeportista());
            pruebasPendientes += tests.stream()
                    .filter(t -> t.getEstadoTest() == EstadoTest.EN_PROGRESO)
                    .count();
        }

        // Reports: count of test sessions for the coach's patients
        long reportes = 0;
        for (Deportista d : pacientes) {
            reportes += testFisicoRepository.findByDeportistaId(d.getIdDeportista()).size();
        }

        return new DashboardCoachResponse.EstadisticasInfo(
                (int) pacientesHoy,
                (int) pruebasPendientes,
                (int) reportes
        );
    }

    /**
     * Gets recent activity (last 5 test sessions created by the coach).
     */
    private List<DashboardCoachResponse.ActividadRecienteItem> getActividadReciente(Long coachId) {
        List<DashboardCoachResponse.ActividadRecienteItem> activity = new ArrayList<>();

        // Get recent test sessions for the coach's patients
        List<Deportista> pacientes = deportistaRepository.findByCoachIdCoach(coachId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        for (Deportista d : pacientes) {
            List<TestFisico> tests = testFisicoRepository.findByDeportistaId(d.getIdDeportista());
            for (TestFisico t : tests) {
                String pacienteNombre = d.getUsuario().getNombres() + " " + d.getUsuario().getApellidos();
                String accion = "Test " + t.getTipoTest().name() + " " +
                        (t.getEstadoTest() == EstadoTest.COMPLETADO ? "completado" : "en progreso");
                String timestamp = t.getFechaEjecucion().format(formatter);
                activity.add(new DashboardCoachResponse.ActividadRecienteItem(
                        pacienteNombre, accion, timestamp, "success"
                ));
            }
        }

        // Sort by timestamp descending and take last 5
        activity.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return activity.size() > 5 ? activity.subList(0, 5) : activity;
    }

    /**
     * Returns a paginated list of recent activity for the coach, ordered by execution date descending.
     * Used by the "Ver toda la actividad" endpoint.
     *
     * @param coachId the coach's ID
     * @param page    zero-based page index
     * @param size    page size
     * @return a DashboardActivityResponse with items and pagination metadata
     */
    public DashboardActivityResponse getActividad(Long coachId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaEjecucion"));
        Page<TestFisico> testPage = testFisicoRepository.findByCoachId(coachId, pageable);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        List<ActividadRecienteItem> items = testPage.getContent().stream()
                .map(t -> {
                    String pacienteNombre = t.getDeportista().getUsuario().getNombres()
                            + " " + t.getDeportista().getUsuario().getApellidos();
                    String accion = "Test " + t.getTipoTest().name() + " "
                            + (t.getEstadoTest() == EstadoTest.COMPLETADO ? "completado" : "en progreso");
                    String timestamp = t.getFechaEjecucion().format(formatter);
                    return new ActividadRecienteItem(pacienteNombre, accion, timestamp, "success");
                })
                .collect(Collectors.toList());

        return new DashboardActivityResponse(
                items,
                testPage.getNumber(),
                testPage.getTotalPages(),
                testPage.getTotalElements()
        );
    }

    /**
     * Gets upcoming appointments for the coach (next 5 future citas).
     */
    private List<DashboardCoachResponse.ProximaCitaCoachItem> getProximasCitasCoach(Long coachId) {
        LocalDateTime now = LocalDateTime.now();
        List<Cita> citas = citaRepository.findByCoachIdAndFechaHoraAfter(coachId, now);

        // Sort ascending and take next 5
        citas.sort(Comparator.comparing(Cita::getFechaHora));
        if (citas.size() > 5) citas = citas.subList(0, 5);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return citas.stream().map(c -> {
            String deportistaNombre = c.getDeportista().getUsuario().getNombres()
                    + " " + c.getDeportista().getUsuario().getApellidos();
            return new DashboardCoachResponse.ProximaCitaCoachItem(
                    deportistaNombre,
                    c.getFechaHora().toLocalDate().format(dateFormatter),
                    c.getFechaHora().toLocalTime().format(timeFormatter),
                    c.getTipoSesion()
            );
        }).collect(Collectors.toList());
    }
}
