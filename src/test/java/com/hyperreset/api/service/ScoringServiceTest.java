package com.hyperreset.api.service;

import com.hyperreset.api.config.ScoringProperties;
import com.hyperreset.api.entity.enums.Calificacion;
import com.hyperreset.api.entity.enums.TipoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScoringService covering all 8 test types,
 * boundary values, and error conditions.
 */
class ScoringServiceTest {

    private ScoringService scoringService;

    @BeforeEach
    void setUp() {
        ScoringProperties props = createTestProperties();
        scoringService = new ScoringService(props);
    }

    private ScoringProperties createTestProperties() {
        ScoringProperties props = new ScoringProperties();
        Map<String, ScoringProperties.ThresholdConfig> thresholds = new HashMap<>();

        // Lower-is-better
        thresholds.put("illinois", createConfig(15.0, 16.0, 17.5, true));
        thresholds.put("velocidad-20m", createConfig(3.0, 3.3, 3.8, true));
        thresholds.put("velocidad-reaccion", createConfig(0.15, 0.20, 0.30, true));

        // Higher-is-better
        thresholds.put("flexion-codos", createConfig(40.0, 30.0, 20.0, false));
        thresholds.put("salto-horizontal", createConfig(240.0, 210.0, 180.0, false));
        thresholds.put("flexion-tronco", createConfig(25.0, 17.0, 8.0, false));
        thresholds.put("dinamometria", createConfig(50.0, 40.0, 30.0, false));
        thresholds.put("andersen", createConfig(2000.0, 1600.0, 1200.0, false));

        props.setThresholds(thresholds);
        return props;
    }

    private ScoringProperties.ThresholdConfig createConfig(double excelenteMax, double buenoMax,
                                                            double regularMax, boolean lowerIsBetter) {
        ScoringProperties.ThresholdConfig config = new ScoringProperties.ThresholdConfig();
        config.setExcelenteMax(BigDecimal.valueOf(excelenteMax));
        config.setBuenoMax(BigDecimal.valueOf(buenoMax));
        config.setRegularMax(BigDecimal.valueOf(regularMax));
        config.setLowerIsBetter(lowerIsBetter);
        return config;
    }

    // ==================================================================
    // All 8 test types — known center values produce correct calificacion
    // ==================================================================

    @ParameterizedTest
    @CsvSource({
        "ILLINOIS, 14.0, EXCELENTE",
        "ILLINOIS, 15.5, BUENO",
        "ILLINOIS, 17.0, REGULAR",
        "ILLINOIS, 18.0, DEFICIENTE",
        "FLEXION_CODOS, 45, EXCELENTE",
        "FLEXION_CODOS, 35, BUENO",
        "FLEXION_CODOS, 25, REGULAR",
        "FLEXION_CODOS, 15, DEFICIENTE",
        "VELOCIDAD_20M, 2.8, EXCELENTE",
        "VELOCIDAD_20M, 3.15, BUENO",
        "VELOCIDAD_20M, 3.5, REGULAR",
        "VELOCIDAD_20M, 4.0, DEFICIENTE",
        "VELOCIDAD_REACCION, 0.12, EXCELENTE",
        "VELOCIDAD_REACCION, 0.18, BUENO",
        "VELOCIDAD_REACCION, 0.25, REGULAR",
        "VELOCIDAD_REACCION, 0.35, DEFICIENTE",
        "SALTO_HORIZONTAL, 250, EXCELENTE",
        "SALTO_HORIZONTAL, 225, BUENO",
        "SALTO_HORIZONTAL, 195, REGULAR",
        "SALTO_HORIZONTAL, 170, DEFICIENTE",
        "FLEXION_TRONCO, 30, EXCELENTE",
        "FLEXION_TRONCO, 21, BUENO",
        "FLEXION_TRONCO, 12, REGULAR",
        "FLEXION_TRONCO, 5, DEFICIENTE",
        "DINAMOMETRIA, 55, EXCELENTE",
        "DINAMOMETRIA, 45, BUENO",
        "DINAMOMETRIA, 35, REGULAR",
        "DINAMOMETRIA, 25, DEFICIENTE",
        "ANDERSEN, 2100, EXCELENTE",
        "ANDERSEN, 1800, BUENO",
        "ANDERSEN, 1400, REGULAR",
        "ANDERSEN, 1000, DEFICIENTE"
    })
    void allTestTypes_ProduceCorrectScore(TipoTest tipoTest, double valor, Calificacion expected) {
        assertEquals(expected, scoringService.calculateScore(tipoTest, valor));
    }

    // ==================================================================
    // Boundary values — exact threshold behavior
    // ==================================================================

    @Test
    void boundary_Velocidad20M_At3_0_IsBUENO() {
        // At exactly 3.0, it should be BUENO (not < 3.0, so not EXCELENTE)
        assertEquals(Calificacion.BUENO, scoringService.calculateScore(TipoTest.VELOCIDAD_20M, 3.0));
    }

    @Test
    void boundary_Velocidad20M_JustBelow3_0_IsEXCELENTE() {
        assertEquals(Calificacion.EXCELENTE, scoringService.calculateScore(TipoTest.VELOCIDAD_20M, 2.999));
    }

    @Test
    void boundary_SaltoHorizontal_At210_IsBUENO() {
        // At exactly 210, it should be BUENO (>= 210 → BUENO)
        assertEquals(Calificacion.BUENO, scoringService.calculateScore(TipoTest.SALTO_HORIZONTAL, 210));
    }

    @Test
    void boundary_SaltoHorizontal_JustBelow210_IsREGULAR() {
        assertEquals(Calificacion.REGULAR, scoringService.calculateScore(TipoTest.SALTO_HORIZONTAL, 209));
    }

    @Test
    void boundary_FlexionCodos_At40_IsBUENO() {
        assertEquals(Calificacion.BUENO, scoringService.calculateScore(TipoTest.FLEXION_CODOS, 40));
    }

    @Test
    void boundary_FlexionCodos_JustAbove40_IsEXCELENTE() {
        assertEquals(Calificacion.EXCELENTE, scoringService.calculateScore(TipoTest.FLEXION_CODOS, 41));
    }

    @Test
    void boundary_Andersen_At1600_IsBUENO() {
        assertEquals(Calificacion.BUENO, scoringService.calculateScore(TipoTest.ANDERSEN, 1600));
    }

    @Test
    void boundary_Andersen_JustBelow1600_IsREGULAR() {
        assertEquals(Calificacion.REGULAR, scoringService.calculateScore(TipoTest.ANDERSEN, 1599));
    }

    // ==================================================================
    // Null / missing TipoTest
    // ==================================================================

    @Test
    void calculateScore_WithNullTipoTest_ThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> scoringService.calculateScore((TipoTest) null, 15.0));
    }

    @Test
    void calculateScore_WithMissingTipoTest_ThrowsIllegalArgument() {
        // Using null to trigger the null check
        assertThrows(IllegalArgumentException.class,
                () -> scoringService.calculateScore(null, BigDecimal.valueOf(15.0)));
    }
}
