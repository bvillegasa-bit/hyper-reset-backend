package com.hyperreset.api.dto.response;

public class ResultadoResponse {

    private Long id;
    private Long testId;
    private String parametro;
    private Double valor;
    private String unidad;
    private String observaciones;

    public ResultadoResponse() {
    }

    public ResultadoResponse(Long id, Long testId, String parametro, Double valor,
                             String unidad, String observaciones) {
        this.id = id;
        this.testId = testId;
        this.parametro = parametro;
        this.valor = valor;
        this.unidad = unidad;
        this.observaciones = observaciones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
