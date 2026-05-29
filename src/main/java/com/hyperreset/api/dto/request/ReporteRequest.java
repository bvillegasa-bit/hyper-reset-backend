package com.hyperreset.api.dto.request;

import jakarta.validation.constraints.NotNull;

public class ReporteRequest {

    @NotNull(message = "Deportista ID is required")
    private Long deportistaId;

    @NotNull(message = "Test Físico ID is required")
    private Long testFisicoId;

    private String tipoReporte;

    public ReporteRequest() {
    }

    public ReporteRequest(Long deportistaId, Long testFisicoId, String tipoReporte) {
        this.deportistaId = deportistaId;
        this.testFisicoId = testFisicoId;
        this.tipoReporte = tipoReporte;
    }

    public Long getDeportistaId() {
        return deportistaId;
    }

    public void setDeportistaId(Long deportistaId) {
        this.deportistaId = deportistaId;
    }

    public Long getTestFisicoId() {
        return testFisicoId;
    }

    public void setTestFisicoId(Long testFisicoId) {
        this.testFisicoId = testFisicoId;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }
}
