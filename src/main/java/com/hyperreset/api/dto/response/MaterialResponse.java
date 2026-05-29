package com.hyperreset.api.dto.response;

import java.time.LocalDate;

public class MaterialResponse {

    private Long id;
    private String titulo;
    private String descripcion;
    private String tipoMaterial;
    private String urlRecurso;
    private LocalDate fechaPublicacion;

    public MaterialResponse() {
    }

    public MaterialResponse(Long id, String titulo, String descripcion, String tipoMaterial,
                            String urlRecurso, LocalDate fechaPublicacion) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoMaterial = tipoMaterial;
        this.urlRecurso = urlRecurso;
        this.fechaPublicacion = fechaPublicacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
}
