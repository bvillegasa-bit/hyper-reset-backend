package com.hyperreset.api.dto.response;

import java.time.LocalDate;

public class TestFisicoResponse {

    private Long id;
    private String deportistaNombre;
    private String tipoTest;
    private LocalDate fechaTest;
    private String notas;
    private boolean completado;

    public TestFisicoResponse() {
    }

    public TestFisicoResponse(Long id, String deportistaNombre, String tipoTest,
                              LocalDate fechaTest, String notas, boolean completado) {
        this.id = id;
        this.deportistaNombre = deportistaNombre;
        this.tipoTest = tipoTest;
        this.fechaTest = fechaTest;
        this.notas = notas;
        this.completado = completado;
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

    public LocalDate getFechaTest() {
        return fechaTest;
    }

    public void setFechaTest(LocalDate fechaTest) {
        this.fechaTest = fechaTest;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}
