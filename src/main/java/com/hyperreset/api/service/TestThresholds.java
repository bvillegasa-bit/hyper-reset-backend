package com.hyperreset.api.service;

import com.hyperreset.api.entity.enums.Calificacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Thresholds for one test type.
 * <p>
 * For lowerIsBetter = true (ILLINOIS, VELOCIDAD_20M, VELOCIDAD_REACCION):
 * <pre>
 *   valor &lt; excelenteMax → EXCELENTE
 *   valor ≤ buenoMax     → BUENO
 *   valor ≤ regularMax   → REGULAR
 *   otherwise            → DEFICIENTE
 * </pre>
 * For lowerIsBetter = false (all other test types):
 * <pre>
 *   valor &gt; buenoMax       → EXCELENTE
 *   valor ≥ regularMin     → BUENO
 *   valor ≥ deficienteMin  → REGULAR
 *   otherwise              → DEFICIENTE
 * </pre>
 * Note: For higher-is-better, the YAML fields map as:
 *   buenoMax   = lower bound for BUENO (regularMin in this class)
 *   regularMax = lower bound for REGULAR (deficienteMin in this class)
 *   excelenteMax = threshold above which is EXCELENTE
 * </pre>
 */
public class TestThresholds {

    private static final Logger log = LoggerFactory.getLogger(TestThresholds.class);

    private final boolean lowerIsBetter;

    // For lowerIsBetter=true:
    private final BigDecimal excelenteMax;   // values < this → EXCELENTE
    private final BigDecimal buenoMax;       // values ≤ this → BUENO
    private final BigDecimal regularMax;     // values ≤ this → REGULAR

    // For lowerIsBetter=false, the same fields map to:
    //   excelenteMax → values > this → EXCELENTE
    //   buenoMax     → values ≥ this → BUENO   (acts as lower bound)
    //   regularMax   → values ≥ this → REGULAR (acts as lower bound)

    /**
     * Creates thresholds for a test type.
     *
     * @param excelenteMax  For lower-is-better: upper bound for EXCELENTE.
     *                      For higher-is-better: threshold above which is EXCELENTE.
     * @param buenoMax      For lower-is-better: upper bound for BUENO.
     *                      For higher-is-better: lower bound for BUENO.
     * @param regularMax    For lower-is-better: upper bound for REGULAR.
     *                      For higher-is-better: lower bound for REGULAR.
     * @param lowerIsBetter Whether lower values produce better scores.
     */
    public TestThresholds(BigDecimal excelenteMax, BigDecimal buenoMax,
                          BigDecimal regularMax, boolean lowerIsBetter) {
        this.excelenteMax = excelenteMax;
        this.buenoMax = buenoMax;
        this.regularMax = regularMax;
        this.lowerIsBetter = lowerIsBetter;
    }

    /**
     * Evaluate a measured value against these thresholds.
     *
     * @param valor the measured value (must be positive)
     * @return the calculated Calificacion
     */
    public Calificacion evaluate(BigDecimal valor) {
        if (lowerIsBetter) {
            return evaluateLowerIsBetter(valor);
        } else {
            return evaluateHigherIsBetter(valor);
        }
    }

    private Calificacion evaluateLowerIsBetter(BigDecimal valor) {
        // Lower is better: < excelenteMax → EXCELENTE, ≤ buenoMax → BUENO, ≤ regularMax → REGULAR
        if (valor.compareTo(excelenteMax) < 0) {
            log.debug("Score EXCELENTE: {} < {}", valor, excelenteMax);
            return Calificacion.EXCELENTE;
        }
        if (valor.compareTo(buenoMax) <= 0) {
            log.debug("Score BUENO: {} <= {}", valor, buenoMax);
            return Calificacion.BUENO;
        }
        if (valor.compareTo(regularMax) <= 0) {
            log.debug("Score REGULAR: {} <= {}", valor, regularMax);
            return Calificacion.REGULAR;
        }
        log.debug("Score DEFICIENTE: {} > {}", valor, regularMax);
        return Calificacion.DEFICIENTE;
    }

    private Calificacion evaluateHigherIsBetter(BigDecimal valor) {
        // Higher is better: > buenoMax → EXCELENTE, ≥ regularMin → BUENO, ≥ deficienteMin → REGULAR
        // buenoMax acts as the threshold for EXCELENTE: values > buenoMax → EXCELENTE
        // regularMax (buenoMax) acts as lower bound for BUENO: values ≥ buenoMax → BUENO
        // regularMax acts as lower bound for REGULAR: values ≥ regularMax → REGULAR
        if (valor.compareTo(excelenteMax) > 0) {
            log.debug("Score EXCELENTE: {} > {}", valor, excelenteMax);
            return Calificacion.EXCELENTE;
        }
        if (valor.compareTo(buenoMax) >= 0) {
            log.debug("Score BUENO: {} >= {}", valor, buenoMax);
            return Calificacion.BUENO;
        }
        if (valor.compareTo(regularMax) >= 0) {
            log.debug("Score REGULAR: {} >= {}", valor, regularMax);
            return Calificacion.REGULAR;
        }
        log.debug("Score DEFICIENTE: {} < {}", valor, regularMax);
        return Calificacion.DEFICIENTE;
    }

    public boolean isLowerIsBetter() {
        return lowerIsBetter;
    }

    public BigDecimal getExcelenteMax() {
        return excelenteMax;
    }

    public BigDecimal getBuenoMax() {
        return buenoMax;
    }

    public BigDecimal getRegularMax() {
        return regularMax;
    }
}
