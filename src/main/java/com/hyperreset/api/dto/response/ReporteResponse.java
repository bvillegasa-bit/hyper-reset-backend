package com.hyperreset.api.dto.response;

import java.time.LocalDate;

public class ReporteResponse {

    private Long id;
    private String deportistaNombre;
    private String tipoTest;
    private LocalDate fechaGeneracion;
    private String tipoReporte;
    private String contenido;

    public ReporteResponse() {
    }

    public ReporteResponse(Long id, String deportistaNombre, String tipoTest,
                           LocalDate fechaGeneracion, String tipoReporte, String contenido) {
        this.id = id;
        this.deportistaNombre = deportistaNombre;
        this.tipoTest = tipoTest;
        this.fechaGeneracion = fechaGeneracion;
        this.tipoReporte = tipoReporte;
        this.contenido = contenido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeportistaNombre() {
        return deportistaNombre;
    }

    public void setDeportistaNombre(String deportistaNombre) {
        this.deportistaNombre = deportistaNombre;
    }

    public String getTipoTest() {
        return tipoTest;
    }

    public void setTipoTest(String tipoTest) {
        this.tipoTest = tipoTest;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
