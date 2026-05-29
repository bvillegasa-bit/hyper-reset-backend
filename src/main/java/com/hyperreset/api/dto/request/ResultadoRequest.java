package com.hyperreset.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResultadoRequest {

    @NotNull(message = "Test ID is required")
    private Long testId;

    @NotBlank(message = "Parameter is required")
    private String parametro;

    @NotNull(message = "Value is required")
    private Double valor;

    @NotBlank(message = "Unit is required")
    private String unidad;

    private String observaciones;

    public ResultadoRequest() {
    }

    public ResultadoRequest(Long testId, String parametro, Double valor, String unidad, String observaciones) {
        this.testId = testId;
        this.parametro = parametro;
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

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
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
