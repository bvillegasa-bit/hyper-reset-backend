package com.hyperreset.api.config;

import com.hyperreset.api.entity.enums.TipoTest;
import com.hyperreset.api.service.TestThresholds;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for test scoring thresholds.
 * Bound from application.yml under "test-scoring.thresholds".
 */
@Component
@ConfigurationProperties(prefix = "test-scoring")
public class ScoringProperties {

    private Map<String, ThresholdConfig> thresholds = new HashMap<>();

    public Map<String, ThresholdConfig> getThresholds() {
        return thresholds;
    }

    public void setThresholds(Map<String, ThresholdConfig> thresholds) {
        this.thresholds = thresholds;
    }

    /**
     * Converts the flat string-keyed configuration into a map of TipoTest → TestThresholds.
     */
    public Map<TipoTest, TestThresholds> toThresholdMap() {
        Map<TipoTest, TestThresholds> result = new HashMap<>();
        for (Map.Entry<String, ThresholdConfig> entry : thresholds.entrySet()) {
            String key = entry.getKey();
            ThresholdConfig config = entry.getValue();
            TipoTest tipoTest = mapKeyToTipoTest(key);
            if (tipoTest != null) {
                result.put(tipoTest, new TestThresholds(
                        config.getExcelenteMax(),
                        config.getBuenoMax(),
                        config.getRegularMax(),
                        config.isLowerIsBetter()
                ));
            }
        }
        return result;
    }

    /**
     * Maps hyphenated YAML keys to TipoTest enum values.
     */
    private TipoTest mapKeyToTipoTest(String key) {
        switch (key.toLowerCase()) {
            case "illinois": return TipoTest.ILLINOIS;
            case "flexion-codos": return TipoTest.FLEXION_CODOS;
            case "velocidad-20m": return TipoTest.VELOCIDAD_20M;
            case "velocidad-reaccion": return TipoTest.VELOCIDAD_REACCION;
            case "salto-horizontal": return TipoTest.SALTO_HORIZONTAL;
            case "flexion-tronco": return TipoTest.FLEXION_TRONCO;
            case "dinamometria": return TipoTest.DINAMOMETRIA;
            case "andersen": return TipoTest.ANDERSEN;
            default: return null;
        }
    }

    public static class ThresholdConfig {
        private BigDecimal excelenteMax;
        private BigDecimal buenoMax;
        private BigDecimal regularMax;
        private boolean lowerIsBetter;

        public BigDecimal getExcelenteMax() {
            return excelenteMax;
        }

        public void setExcelenteMax(BigDecimal excelenteMax) {
            this.excelenteMax = excelenteMax;
        }

        public BigDecimal getBuenoMax() {
            return buenoMax;
        }

        public void setBuenoMax(BigDecimal buenoMax) {
            this.buenoMax = buenoMax;
        }

        public BigDecimal getRegularMax() {
            return regularMax;
        }

        public void setRegularMax(BigDecimal regularMax) {
            this.regularMax = regularMax;
        }

        public boolean isLowerIsBetter() {
            return lowerIsBetter;
        }

        public void setLowerIsBetter(boolean lowerIsBetter) {
            this.lowerIsBetter = lowerIsBetter;
        }
    }
}
