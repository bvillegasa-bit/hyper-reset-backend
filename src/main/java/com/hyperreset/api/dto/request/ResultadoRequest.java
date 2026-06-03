package com.hyperreset.api.dto.request;

import com.hyperreset.api.entity.enums.TipoTest;
import jakarta.validation.constraints.NotNull;

public class ResultadoRequest {

    @NotNull(message = "Test ID is required")
    private Long testId;

    @NotNull(message = "Tipo de test is required")
    private TipoTest tipoTest;

    @NotNull(message = "Value is required")
    private Double valor;

    @NotNull(message = "Unit is required")
    private String unidad;

    private String observaciones;

    public ResultadoRequest() {
    }

    public ResultadoRequest(Long testId, TipoTest tipoTest, Double valor, String unidad, String observaciones) {
        this.testId = testId;
        this.tipoTest = tipoTest;
        this.valor = valor;
        this.unidad = unidad;
        this.observaciones = observaciones;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public TipoTest getTipoTest() {
        return tipoTest;
    }

    public void setTipoTest(TipoTest tipoTest) {
        this.tipoTest = tipoTest;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
