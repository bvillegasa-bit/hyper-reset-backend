package com.hyperreset.api.dto.response;

import java.time.LocalDate;

public class CitaResponse {

    private Long id;
    private String coachNombre;
    private String deportistaNombre;
    private LocalDate fechaCita;
    private String horaCita;
    private String estado;
    private String motivo;
    private String notas;

    public CitaResponse() {
    }

    public CitaResponse(Long id, String coachNombre, String deportistaNombre, LocalDate fechaCita,
                        String horaCita, String estado, String motivo, String notas) {
        this.id = id;
        this.coachNombre = coachNombre;
        this.deportistaNombre = deportistaNombre;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.estado = estado;
        this.motivo = motivo;
        this.notas = notas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCoachNombre() {
        return coachNombre;
    }

    public void setCoachNombre(String coachNombre) {
        this.coachNombre = coachNombre;
    }

    public String getDeportistaNombre() {
        return deportistaNombre;
    }

    public void setDeportistaNombre(String deportistaNombre) {
        this.deportistaNombre = deportistaNombre;
    }

    public LocalDate getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDate fechaCita) {
        this.fechaCita = fechaCita;
    }

    public String getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(String horaCita) {
        this.horaCita = horaCita;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
