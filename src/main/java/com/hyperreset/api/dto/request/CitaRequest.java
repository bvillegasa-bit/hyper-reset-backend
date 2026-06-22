package com.hyperreset.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CitaRequest {

    @NotNull(message = "Coach ID is required")
    private Long coachId;

    @NotNull(message = "Deportista ID is required")
    private Long deportistaId;

    @NotNull(message = "Fecha de cita is required")
    private LocalDate fechaCita;

    @NotBlank(message = "Hora de cita is required")
    private String horaCita;

    private String motivo;

    private String notas;

    public CitaRequest() {
    }

    public CitaRequest(Long coachId, Long deportistaId, LocalDate fechaCita,
                       String horaCita, String motivo, String notas) {
        this.coachId = coachId;
        this.deportistaId = deportistaId;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.motivo = motivo;
        this.notas = notas;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Long getDeportistaId() {
        return deportistaId;
    }

    public void setDeportistaId(Long deportistaId) {
        this.deportistaId = deportistaId;
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
