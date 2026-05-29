package com.hyperreset.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MensajeRequest {

    @NotNull(message = "Destinatario ID is required")
    private Long destinatarioId;

    @NotBlank(message = "Content is required")
    private String contenido;

    public MensajeRequest() {
    }

    public MensajeRequest(Long destinatarioId, String contenido) {
        this.destinatarioId = destinatarioId;
        this.contenido = contenido;
    }

    public Long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
