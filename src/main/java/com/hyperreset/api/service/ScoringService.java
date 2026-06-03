package com.hyperreset.api.service;

import com.hyperreset.api.config.ScoringProperties;
import com.hyperreset.api.entity.enums.Calificacion;
import com.hyperreset.api.entity.enums.TipoTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * Stateless service that calculates a Calificacion (EXCELENTE/BUENO/REGULAR/DEFICIENTE)
 * for a given test type and measured value.
 * <p>
 * Thresholds are loaded at construction time from application.yml via ScoringProperties.
 */
@Service
public class ScoringService {

    private static final Logger log = LoggerFactory.getLogger(ScoringService.class);

    private final Map<TipoTest, TestThresholds> thresholds;

    public ScoringService(ScoringProperties scoringProperties) {
        this.thresholds = Collections.unmodifiableMap(scoringProperties.toThresholdMap());
        log.info("ScoringService initialized with {} test type thresholds", thresholds.size());
    }

    /**
     * Calculate the calificacion for a given test type and measured value.
     *
     * @param tipoTest     the type of physical test (ILLINOIS, FLEXION_CODOS, etc.)
     * @param valorObtenido the measured value (must be positive)
     * @return the calculated Calificacion
     * @throws IllegalArgumentException if no thresholds are configured for the given tipoTest
     */
    public Calificacion calculateScore(TipoTest tipoTest, BigDecimal valorObtenido) {
        if (tipoTest == null) {
            throw new IllegalArgumentException("TipoTest must not be null");
        }

        TestThresholds t = thresholds.get(tipoTest);
        if (t == null) {
            throw new IllegalArgumentException("No thresholds configured for test type: " + tipoTest);
        }

        Calificacion result = t.evaluate(valorObtenido);
        log.debug("Score for {} = {} → {}", tipoTest, valorObtenido, result);
        return result;
    }

    /**
     * Convenience method using double value.
     */
    public Calificacion calculateScore(TipoTest tipoTest, double valorObtenido) {
        return calculateScore(tipoTest, BigDecimal.valueOf(valorObtenido));
    }

    /**
     * Returns the immutable thresholds map (for testing/inspection).
     */
    public Map<TipoTest, TestThresholds> getThresholds() {
        return thresholds;
    }
}
