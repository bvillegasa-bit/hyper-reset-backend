package com.hyperreset.api.service;

import com.hyperreset.api.dto.request.ResultadoRequest;
import com.hyperreset.api.dto.response.ResultadoResponse;
import com.hyperreset.api.dto.response.TipoTestEstadoResponse;
import com.hyperreset.api.entity.ResultadoTest;
import com.hyperreset.api.entity.TestFisico;
import com.hyperreset.api.entity.enums.Calificacion;
import com.hyperreset.api.entity.enums.TipoTest;
import com.hyperreset.api.exception.BadRequestException;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.ResultadoTestRepository;
import com.hyperreset.api.repository.TestFisicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResultadoTestService {

    private static final Logger log = LoggerFactory.getLogger(ResultadoTestService.class);

    @Autowired
    private ResultadoTestRepository resultadoTestRepository;

    @Autowired
    private TestFisicoRepository testFisicoRepository;

    @Autowired
    private ScoringService scoringService;

    @Transactional(readOnly = true)
    public List<ResultadoResponse> getResultadosByTest(Long testId) {
        // Verify test exists
        testFisicoRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("TestFisico", "id", testId));

        List<ResultadoTest> resultados = resultadoTestRepository.findByTestFisicoIdTestFisico(testId);
        return resultados.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResultadoResponse> getResultadosByDeportista(Long deportistaId) {
        List<ResultadoTest> resultados = resultadoTestRepository.findByTestFisicoDeportistaId(deportistaId);
        return resultados.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ResultadoResponse createResultado(ResultadoRequest request) {
        log.debug("Creating new Resultado for test ID: {}", request.getTestId());

        TestFisico testFisico = testFisicoRepository.findById(request.getTestId())
                .orElseThrow(() -> new ResourceNotFoundException("TestFisico", "id", request.getTestId()));

        // Validate valorObtenido is positive
        if (request.getValor() == null || request.getValor() <= 0) {
            throw new BadRequestException("El valor obtenido debe ser positivo");
        }

        // Map the simple request to the entity
        ResultadoTest resultado = new ResultadoTest();
        resultado.setTestFisico(testFisico);
        resultado.setTipoTest(request.getTipoTest());
        resultado.setValorObtenido(BigDecimal.valueOf(request.getValor()));
        resultado.setUnidadMedicion(request.getUnidad());
        resultado.setObservaciones(request.getObservaciones());

        // Auto-calculate calificacion using ScoringService
        try {
            Calificacion cal = scoringService.calculateScore(
                    testFisico.getTipoTest(),
                    resultado.getValorObtenido());
            resultado.setCalificacion(cal);
            log.debug("Auto-calculated calificacion: {} for test type: {}",
                    cal, testFisico.getTipoTest());
        } catch (Exception e) {
            log.warn("Could not calculate calificacion: {}", e.getMessage());
        }

        resultado = resultadoTestRepository.save(resultado);
        log.debug("Resultado created with ID: {}", resultado.getIdResultado());

        return mapToResponse(resultado);
    }

    public void deleteResultado(Long id) {
        log.debug("Deleting Resultado with ID: {}", id);

        ResultadoTest resultado = resultadoTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResultadoTest", "id", id));

        resultadoTestRepository.delete(resultado);
        log.debug("Resultado {} deleted", id);
    }

    // ==================================================================
    // Tipos de Test con Estado (8 fixed types)
    // ==================================================================

    /**
     * Metadata for the 8 fixed test types: label, emoji icon, estimated duration.
     */
    private static final List<TipoTestMeta> TEST_TYPES = List.of(
        new TipoTestMeta("ILLINOIS", "Test de Illinois", "🏃", "60s", "segundos"),
        new TipoTestMeta("FLEXION_CODOS", "Flexión/Extensión de codos", "💪", "30s", "repeticiones"),
        new TipoTestMeta("VELOCIDAD_20M", "Velocidad 20 metros", "⚡", "10s", "segundos"),
        new TipoTestMeta("VELOCIDAD_REACCION", "Velocidad de reacción", "👆", "15s", "milisegundos"),
        new TipoTestMeta("SALTO_HORIZONTAL", "Salto horizontal", "🦘", "5s", "centímetros"),
        new TipoTestMeta("FLEXION_TRONCO", "Flexión profunda de tronco", "🧘", "30s", "centímetros"),
        new TipoTestMeta("DINAMOMETRIA", "Dinamometría", "🤜", "20s", "kg"),
        new TipoTestMeta("ANDERSEN", "Test de Andersen", "🏃‍♂️", "10min", "metros")
    );

    /**
     * Returns the 8 fixed test types with completion status for a given deportista.
     * For each type, checks if the deportista has at least one ResultadoTest record.
     * If yes, includes the latest value and date; otherwise shows as pending.
     */
    @Transactional(readOnly = true)
    public List<TipoTestEstadoResponse> getTiposTestConEstado(Long deportistaId) {
        log.debug("Fetching test type status for deportistaId: {}", deportistaId);

        // Get all resultados for this deportista, grouped by tipoTest
        List<ResultadoTest> resultados = resultadoTestRepository.findByTestFisicoDeportistaId(deportistaId);

        // Group by tipoTest and find the latest result per type
        Map<TipoTest, ResultadoTest> latestByType = resultados.stream()
                .collect(Collectors.toMap(
                        ResultadoTest::getTipoTest,
                        r -> r,
                        (existing, replacement) -> {
                            // Keep the one with the later test execution date
                            if (existing.getTestFisico().getFechaEjecucion()
                                    .compareTo(replacement.getTestFisico().getFechaEjecucion()) >= 0) {
                                return existing;
                            }
                            return replacement;
                        }
                ));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<TipoTestEstadoResponse> response = new ArrayList<>();

        for (TipoTestMeta meta : TEST_TYPES) {
            TipoTest tipoTestEnum = TipoTest.valueOf(meta.tipoTest);
            ResultadoTest latest = latestByType.get(tipoTestEnum);

            boolean completado = latest != null;
            Double ultimoValor = completado ? latest.getValorObtenido().doubleValue() : null;
            String fechaUltimo = null;
            if (completado && latest.getTestFisico().getFechaEjecucion() != null) {
                fechaUltimo = latest.getTestFisico().getFechaEjecucion().toLocalDate().format(dateFormatter);
            }

            response.add(new TipoTestEstadoResponse(
                    meta.tipoTest,
                    meta.nombre,
                    meta.icono,
                    meta.duracion,
                    completado,
                    ultimoValor,
                    completado ? meta.unidad : null,
                    fechaUltimo
            ));
        }

        return response;
    }

    /**
     * Internal helper record/class to hold fixed test type metadata.
     */
    private static class TipoTestMeta {
        final String tipoTest;
        final String nombre;
        final String icono;
        final String duracion;
        final String unidad;

        TipoTestMeta(String tipoTest, String nombre, String icono, String duracion, String unidad) {
            this.tipoTest = tipoTest;
            this.nombre = nombre;
            this.icono = icono;
            this.duracion = duracion;
            this.unidad = unidad;
        }
    }

    private ResultadoResponse mapToResponse(ResultadoTest resultado) {
        return new ResultadoResponse(
                resultado.getIdResultado(),
                resultado.getTestFisico().getIdTestFisico(),
                resultado.getTipoTest() != null ? resultado.getTipoTest().name() : null,
                resultado.getValorObtenido().doubleValue(),
                resultado.getUnidadMedicion(),
                resultado.getCalificacion() != null ? resultado.getCalificacion().name() : null,
                resultado.getObservaciones()
        );
    }
}
