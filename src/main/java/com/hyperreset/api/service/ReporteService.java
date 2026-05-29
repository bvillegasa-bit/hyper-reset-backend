package com.hyperreset.api.service;

import com.hyperreset.api.dto.request.ReporteRequest;
import com.hyperreset.api.dto.response.ReporteResponse;
import com.hyperreset.api.entity.Coach;
import com.hyperreset.api.entity.Reporte;
import com.hyperreset.api.entity.TestFisico;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.CoachRepository;
import com.hyperreset.api.repository.ReporteRepository;
import com.hyperreset.api.repository.TestFisicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteService.class);

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private TestFisicoRepository testFisicoRepository;

    @Autowired
    private CoachRepository coachRepository;

    public ReporteResponse getReporteById(Long id) {
        log.debug("Fetching reporte by id: {}", id);
        Reporte reporte = reporteRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte", "id", id));
        return toReporteResponse(reporte);
    }

    @Transactional(readOnly = true)
    public List<ReporteResponse> getReportesByDeportista(Long deportistaId) {
        log.debug("Fetching reportes by deportista id: {}", deportistaId);
        return reporteRepository.findByDeportistaId(deportistaId).stream()
                .map(this::toReporteResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReporteResponse> getReportesByTest(Long testId) {
        log.debug("Fetching reportes by test id: {}", testId);
        return reporteRepository.findByTestFisicoId(testId).stream()
                .map(this::toReporteResponse)
                .collect(Collectors.toList());
    }

    public ReporteResponse generarReporte(ReporteRequest request, Long coachUserId) {
        log.debug("Generating reporte for testId: {} by coachUserId: {}",
                request.getTestFisicoId(), coachUserId);

        TestFisico testFisico = testFisicoRepository.findByIdWithDeportista(request.getTestFisicoId())
                .orElseThrow(() -> new ResourceNotFoundException("TestFisico", "id", request.getTestFisicoId()));

        Coach coach = coachRepository.findByUsuarioId(coachUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach", "usuarioId", coachUserId));

        Reporte reporte = new Reporte(testFisico, coach);

        String observaciones = "Reporte generado para el test físico del deportista "
                + testFisico.getDeportista().getUsuario().getNombres() + " "
                + testFisico.getDeportista().getUsuario().getApellidos()
                + ". Tipo de reporte: " + (request.getTipoReporte() != null ? request.getTipoReporte() : "General");
        reporte.setObservacionesReporte(observaciones);

        reporte.setRecomendacionesReporte("Recomendaciones pendientes de evaluación completa.");

        Reporte saved = reporteRepository.save(reporte);
        log.info("Reporte generated with id: {}", saved.getIdReporte());

        return getReporteById(saved.getIdReporte());
    }

    public void deleteReporte(Long id) {
        log.debug("Deleting reporte with id: {}", id);
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte", "id", id));
        reporteRepository.delete(reporte);
        log.info("Deleted reporte with id: {}", id);
    }

    private ReporteResponse toReporteResponse(Reporte reporte) {
        ReporteResponse response = new ReporteResponse();
        response.setId(reporte.getIdReporte());

        String deportistaNombre = reporte.getTestFisico().getDeportista().getUsuario().getNombres()
                + " " + reporte.getTestFisico().getDeportista().getUsuario().getApellidos();
        response.setDeportistaNombre(deportistaNombre);

        response.setTipoTest("Test Físico General");
        response.setFechaGeneracion(reporte.getFechaGeneracion().toLocalDate());
        response.setTipoReporte("General");

        String contenido = (reporte.getObservacionesReporte() != null ? reporte.getObservacionesReporte() : "")
                + "\n\n---\n\n"
                + (reporte.getRecomendacionesReporte() != null ? reporte.getRecomendacionesReporte() : "");
        response.setContenido(contenido);

        return response;
    }
}
