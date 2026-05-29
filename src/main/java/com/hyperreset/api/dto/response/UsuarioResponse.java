package com.hyperreset.api.dto.response;

import java.time.LocalDate;

public class UsuarioResponse {

    private Long id;
    private String email;
    private String rol;
    private String nombre;
    private LocalDate fechaRegistro;
    private boolean activo;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, String email, String rol, String nombre,
                           LocalDate fechaRegistro, boolean activo) {
        this.id = id;
        this.email = email;
        this.rol = rol;
        this.nombre = nombre;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
