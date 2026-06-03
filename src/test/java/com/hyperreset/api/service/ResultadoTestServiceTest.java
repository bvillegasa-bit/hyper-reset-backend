package com.hyperreset.api.service;

import com.hyperreset.api.dto.request.ResultadoRequest;
import com.hyperreset.api.dto.response.ResultadoResponse;
import com.hyperreset.api.entity.Coach;
import com.hyperreset.api.entity.Deportista;
import com.hyperreset.api.entity.ResultadoTest;
import com.hyperreset.api.entity.TestFisico;
import com.hyperreset.api.entity.enums.Calificacion;
import com.hyperreset.api.entity.enums.TipoTest;
import com.hyperreset.api.exception.BadRequestException;
import com.hyperreset.api.repository.ResultadoTestRepository;
import com.hyperreset.api.repository.TestFisicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ResultadoTestService — auto-calificacion, validation, tipoTest mapping.
 */
@ExtendWith(MockitoExtension.class)
class ResultadoTestServiceTest {

    @InjectMocks
    private ResultadoTestService service;

    @Mock
    private ResultadoTestRepository resultadoTestRepository;

    @Mock
    private TestFisicoRepository testFisicoRepository;

    @Mock
    private ScoringService scoringService;

    @Captor
    private ArgumentCaptor<ResultadoTest> resultadoCaptor;

    private TestFisico testFisico;

    @BeforeEach
    void setUp() {
        Deportista deportista = new Deportista();
        Coach coach = new Coach();
        testFisico = new TestFisico(deportista, coach, TipoTest.FLEXION_CODOS);
        setId(testFisico, "idTestFisico", 1L);
    }

    private void setId(Object entity, String fieldName, Long id) {
        try {
            java.lang.reflect.Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set id: " + fieldName, e);
        }
    }

    @Test
    void createResultado_SetsCalificacionFromScoringService() {
        // Arrange
        ResultadoRequest request = new ResultadoRequest(1L, TipoTest.FLEXION_CODOS,
                35.0, "repeticiones", "Primer intento");

        when(testFisicoRepository.findById(1L)).thenReturn(Optional.of(testFisico));
        when(scoringService.calculateScore(TipoTest.FLEXION_CODOS, BigDecimal.valueOf(35.0)))
                .thenReturn(Calificacion.BUENO);

        ResultadoTest savedResultado = new ResultadoTest();
        savedResultado.setIdResultado(100L);
        savedResultado.setTestFisico(testFisico);
        savedResultado.setTipoTest(TipoTest.FLEXION_CODOS);
        savedResultado.setValorObtenido(BigDecimal.valueOf(35.0));
        savedResultado.setUnidadMedicion("repeticiones");
        savedResultado.setCalificacion(Calificacion.BUENO);
        savedResultado.setObservaciones("Primer intento");

        when(resultadoTestRepository.save(any())).thenReturn(savedResultado);

        // Act
        ResultadoResponse response = service.createResultado(request);

        // Assert
        verify(scoringService).calculateScore(TipoTest.FLEXION_CODOS, BigDecimal.valueOf(35.0));
        assertNotNull(response.getCalificacion());
        assertEquals("BUENO", response.getCalificacion());
    }

    @Test
    void createResultado_WithValorZero_ThrowsBadRequest() {
        // Arrange
        ResultadoRequest request = new ResultadoRequest(1L, TipoTest.FLEXION_CODOS,
                0.0, "repeticiones", null);

        when(testFisicoRepository.findById(1L)).thenReturn(Optional.of(testFisico));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.createResultado(request));
        assertEquals("El valor obtenido debe ser positivo", exception.getMessage());
        verify(resultadoTestRepository, never()).save(any());
    }

    @Test
    void createResultado_WithNegativeValor_ThrowsBadRequest() {
        // Arrange
        ResultadoRequest request = new ResultadoRequest(1L, TipoTest.FLEXION_CODOS,
                -5.0, "repeticiones", null);

        when(testFisicoRepository.findById(1L)).thenReturn(Optional.of(testFisico));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> service.createResultado(request));
        verify(resultadoTestRepository, never()).save(any());
    }

    @Test
    void createResultado_SetsTipoTestFromRequest() {
        // Arrange
        ResultadoRequest request = new ResultadoRequest(1L, TipoTest.ILLINOIS,
                15.2, "segundos", null);

        when(testFisicoRepository.findById(1L)).thenReturn(Optional.of(testFisico));
        when(scoringService.calculateScore(any(), any())).thenReturn(Calificacion.BUENO);

        ResultadoTest savedResultado = new ResultadoTest();
        savedResultado.setIdResultado(101L);
        savedResultado.setTestFisico(testFisico);
        savedResultado.setTipoTest(TipoTest.ILLINOIS);
        savedResultado.setValorObtenido(BigDecimal.valueOf(15.2));
        savedResultado.setUnidadMedicion("segundos");
        savedResultado.setCalificacion(Calificacion.BUENO);
        when(resultadoTestRepository.save(any())).thenReturn(savedResultado);

        // Act
        ResultadoResponse response = service.createResultado(request);

        // Assert
        verify(resultadoTestRepository).save(resultadoCaptor.capture());
        ResultadoTest captured = resultadoCaptor.getValue();
        assertEquals(TipoTest.ILLINOIS, captured.getTipoTest());
    }

    @Test
    void mapToResponse_IncludesCalificacion() {
        // Arrange — invoke via reflection to test private method
        ResultadoTest resultado = new ResultadoTest();
        resultado.setIdResultado(100L);
        resultado.setTestFisico(testFisico);
        resultado.setTipoTest(TipoTest.ILLINOIS);
        resultado.setValorObtenido(BigDecimal.valueOf(15.2));
        resultado.setUnidadMedicion("segundos");
        resultado.setCalificacion(Calificacion.EXCELENTE);
        resultado.setObservaciones("Buen intento");

        ResultadoResponse response = invokeMapToResponse(resultado);

        // Assert
        assertNotNull(response.getCalificacion());
        assertEquals("EXCELENTE", response.getCalificacion());
        assertEquals("ILLINOIS", response.getTipoTest());
    }

    @Test
    void createResultado_CallsScoringServiceWithCorrectParameters() {
        // Arrange
        ResultadoRequest request = new ResultadoRequest(1L, TipoTest.ILLINOIS,
                14.5, "segundos", null);

        when(testFisicoRepository.findById(1L)).thenReturn(Optional.of(testFisico));
        when(scoringService.calculateScore(TipoTest.FLEXION_CODOS, BigDecimal.valueOf(14.5)))
                .thenReturn(Calificacion.EXCELENTE);

        ResultadoTest savedResultado = new ResultadoTest();
        savedResultado.setIdResultado(102L);
        savedResultado.setTestFisico(testFisico);
        savedResultado.setTipoTest(TipoTest.ILLINOIS);
        savedResultado.setValorObtenido(BigDecimal.valueOf(14.5));
        savedResultado.setUnidadMedicion("segundos");
        savedResultado.setCalificacion(Calificacion.EXCELENTE);
        when(resultadoTestRepository.save(any())).thenReturn(savedResultado);

        // Act
        service.createResultado(request);

        // Assert: scoring service was called with the TestFisico's tipoTest (FLEXION_CODOS),
        // not the request's tipoTest (ILLINOIS), because the test session determines the type
        verify(scoringService).calculateScore(TipoTest.FLEXION_CODOS, BigDecimal.valueOf(14.5));
    }

    private ResultadoResponse invokeMapToResponse(ResultadoTest resultado) {
        try {
            java.lang.reflect.Method method = ResultadoTestService.class
                    .getDeclaredMethod("mapToResponse", ResultadoTest.class);
            method.setAccessible(true);
            return (ResultadoResponse) method.invoke(service, resultado);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke mapToResponse", e);
        }
    }
}
