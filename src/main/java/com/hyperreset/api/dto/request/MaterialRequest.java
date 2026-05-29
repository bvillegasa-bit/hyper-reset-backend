package com.hyperreset.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public class MaterialRequest {

    @NotBlank(message = "Title is required")
    private String titulo;

    private String descripcion;

    private String tipoMaterial;

    private String urlRecurso;

    public MaterialRequest() {
    }

    public MaterialRequest(String titulo, String descripcion, String tipoMaterial, String urlRecurso) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoMaterial = tipoMaterial;
        this.urlRecurso = urlRecurso;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public String getUrlRecurso() {
        return urlRecurso;
    }

    public void setUrlRecurso(String urlRecurso) {
        this.urlRecurso = urlRecurso;
    }
}
