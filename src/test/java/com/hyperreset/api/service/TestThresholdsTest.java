package com.hyperreset.api.service;

import com.hyperreset.api.entity.enums.Calificacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for TestThresholds.evaluate() covering boundary values
 * for both lower-is-better and higher-is-better scoring directions.
 */
class TestThresholdsTest {

    // ==================================================================
    // Lower-is-better: ILLINOIS thresholds (excelenteMax=15.0, buenoMax=16.0, regularMax=17.5)
    // ==================================================================

    private TestThresholds lowerIsBetter() {
        return new TestThresholds(
                BigDecimal.valueOf(15.0),   // excelenteMax
                BigDecimal.valueOf(16.0),   // buenoMax
                BigDecimal.valueOf(17.5),   // regularMax
                true                        // lowerIsBetter
        );
    }

    @ParameterizedTest
    @CsvSource({
        "14.5, EXCELENTE",
        "14.9, EXCELENTE",
        "15.0, BUENO",       // boundary: exactly at excelenteMax → BUENO (not < excelenteMax)
        "15.5, BUENO",
        "16.0, BUENO",       // boundary: exactly at buenoMax → BUENO
        "16.1, REGULAR",
        "17.0, REGULAR",
        "17.5, REGULAR",     // boundary: exactly at regularMax → REGULAR
        "17.6, DEFICIENTE",
        "20.0, DEFICIENTE"
    })
    void lowerIsBetter_ReturnsCorrectCalificacion(double valor, Calificacion expected) {
        assertEquals(expected, lowerIsBetter().evaluate(BigDecimal.valueOf(valor)));
    }

    // ==================================================================
    // Higher-is-better: FLEXION_CODOS (excelenteMax=40, buenoMax=30, regularMax=20)
    // ==================================================================

    private TestThresholds higherIsBetter() {
        return new TestThresholds(
                BigDecimal.valueOf(40),   // excelenteMax
                BigDecimal.valueOf(30),   // buenoMax (acts as regularMin)
                BigDecimal.valueOf(20),   // regularMax (acts as deficienteMin)
                false                     // lowerIsBetter
        );
    }

    @ParameterizedTest
    @CsvSource({
        "41, EXCELENTE",
        "45, EXCELENTE",
        "40, BUENO",         // boundary: exactly at excelenteMax → BUENO (not > excelenteMax)
        "35, BUENO",
        "30, BUENO",         // boundary: exactly at buenoMax → BUENO
        "29, REGULAR",
        "25, REGULAR",
        "20, REGULAR",       // boundary: exactly at regularMax → REGULAR
        "19, DEFICIENTE",
        "10, DEFICIENTE"
    })
    void higherIsBetter_ReturnsCorrectCalificacion(double valor, Calificacion expected) {
        assertEquals(expected, higherIsBetter().evaluate(BigDecimal.valueOf(valor)));
    }

    // ==================================================================
    // All 8 test types center values
    // ==================================================================

    @Test
    void illinois_CenterValue_ReturnsCorrect() {
        TestThresholds t = new TestThresholds(BigDecimal.valueOf(15.0), BigDecimal.valueOf(16.0),
                BigDecimal.valueOf(17.5), true);
        assertEquals(Calificacion.EXCELENTE, t.evaluate(BigDecimal.valueOf(14.0)));
        assertEquals(Calificacion.BUENO, t.evaluate(BigDecimal.valueOf(15.5)));
        assertEquals(Calificacion.REGULAR, t.evaluate(BigDecimal.valueOf(16.8)));
        assertEquals(Calificacion.DEFICIENTE, t.evaluate(BigDecimal.valueOf(18.0)));
    }

    @Test
    void flexionCodos_CenterValue_ReturnsCorrect() {
        TestThresholds t = new TestThresholds(BigDecimal.valueOf(40), BigDecimal.valueOf(30),
                BigDecimal.valueOf(20), false);
        assertEquals(Calificacion.EXCELENTE, t.evaluate(BigDecimal.valueOf(45)));
        assertEquals(Calificacion.BUENO, t.evaluate(BigDecimal.valueOf(35)));
        assertEquals(Calificacion.REGULAR, t.evaluate(BigDecimal.valueOf(25)));
        assertEquals(Calificacion.DEFICIENTE, t.evaluate(BigDecimal.valueOf(15)));
    }

    @Test
    void velocidad20M_CenterValue_ReturnsCorrect() {
        TestThresholds t = new TestThresholds(BigDecimal.valueOf(3.0), BigDecimal.valueOf(3.3),
                BigDecimal.valueOf(3.8), true);
        assertEquals(Calificacion.EXCELENTE, t.evaluate(BigDecimal.valueOf(2.8)));
        assertEquals(Calificacion.BUENO, t.evaluate(BigDecimal.valueOf(3.15)));
        assertEquals(Calificacion.REGULAR, t.evaluate(BigDecimal.valueOf(3.5)));
        assertEquals(Calificacion.DEFICIENTE, t.evaluate(BigDecimal.valueOf(4.0)));
    }

    @Test
    void velocidadReaccion_CenterValue_ReturnsCorrect() {
        TestThresholds t = new TestThresholds(BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.20),
                BigDecimal.valueOf(0.30), true);
        assertEquals(Calificacion.EXCELENTE, t.evaluate(BigDecimal.valueOf(0.12)));
        assertEquals(Calificacion.BUENO, t.evaluate(BigDecimal.valueOf(0.18)));
        assertEquals(Calificacion.REGULAR, t.evaluate(BigDecimal.valueOf(0.25)));
        assertEquals(Calificacion.DEFICIENTE, t.evaluate(BigDecimal.valueOf(0.35)));
    }

    @Test
    void saltoHorizontal_CenterValue_ReturnsCorrect() {
        TestThresholds t = new TestThresholds(BigDecimal.valueOf(240), BigDecimal.valueOf(210),
                BigDecimal.valueOf(180), false);
        assertEquals(Calificacion.EXCELENTE, t.evaluate(BigDecimal.valueOf(250)));
        assertEquals(Calificacion.BUENO, t.evaluate(BigDecimal.valueOf(225)));
        assertEquals(Calificacion.REGULAR, t.evaluate(BigDecimal.valueOf(195)));
        assertEquals(Calificacion.DEFICIENTE, t.evaluate(BigDecimal.valueOf(170)));
    }

    @Test
    void flexionTronco_CenterValue_ReturnsCorrect() {
        TestThresholds t = new TestThresholds(BigDecimal.valueOf(25), BigDecimal.valueOf(17),
                BigDecimal.valueOf(8), false);
        assertEquals(Calificacion.EXCELENTE, t.evaluate(BigDecimal.valueOf(30)));
        assertEquals(Calificacion.BUENO, t.evaluate(BigDecimal.valueOf(21)));
        assertEquals(Calificacion.REGULAR, t.evaluate(BigDecimal.valueOf(12)));
        assertEquals(Calificacion.DEFICIENTE, t.evaluate(BigDecimal.valueOf(5)));
    }

    @Test
    void dinamometria_CenterValue_ReturnsCorrect() {
        TestThresholds t = new TestThresholds(BigDecimal.valueOf(50), BigDecimal.valueOf(40),
                BigDecimal.valueOf(30), false);
        assertEquals(Calificacion.EXCELENTE, t.evaluate(BigDecimal.valueOf(55)));
        assertEquals(Calificacion.BUENO, t.evaluate(BigDecimal.valueOf(45)));
        assertEquals(Calificacion.REGULAR, t.evaluate(BigDecimal.valueOf(35)));
        assertEquals(Calificacion.DEFICIENTE, t.evaluate(BigDecimal.valueOf(25)));
    }

    @Test
    void andersen_CenterValue_ReturnsCorrect() {
        TestThresholds t = new TestThresholds(BigDecimal.valueOf(2000), BigDecimal.valueOf(1600),
                BigDecimal.valueOf(1200), false);
        assertEquals(Calificacion.EXCELENTE, t.evaluate(BigDecimal.valueOf(2100)));
        assertEquals(Calificacion.BUENO, t.evaluate(BigDecimal.valueOf(1800)));
        assertEquals(Calificacion.REGULAR, t.evaluate(BigDecimal.valueOf(1400)));
        assertEquals(Calificacion.DEFICIENTE, t.evaluate(BigDecimal.valueOf(1000)));
    }
}
