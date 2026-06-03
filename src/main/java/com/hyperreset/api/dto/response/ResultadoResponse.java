package com.hyperreset.api.dto.response;

public class ResultadoResponse {

    private Long id;
    private Long testId;
    private String tipoTest;
    private Double valor;
    private String unidad;
    private String calificacion;
    private String observaciones;

    public ResultadoResponse() {
    }

    public ResultadoResponse(Long id, Long testId, String tipoTest, Double valor,
                             String unidad, String calificacion, String observaciones) {
        this.id = id;
        this.testId = testId;
        this.tipoTest = tipoTest;
        this.valor = valor;
        this.unidad = unidad;
        this.calificacion = calificacion;
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

    public String getTipoTest() {
        return tipoTest;
    }

    public void setTipoTest(String tipoTest) {
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

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
