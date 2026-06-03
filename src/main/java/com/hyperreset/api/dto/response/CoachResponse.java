package com.hyperreset.api.dto.response;

import java.time.LocalDate;

public class CoachResponse {

    private Long idCoach;
    private Long usuarioId;
    private String nombreCompleto;
    private String email;
    private String especialidad;
    private String descripcion;
    private String telefono;
    private LocalDate fechaRegistro;

    public CoachResponse() {
    }

    public CoachResponse(Long idCoach, String nombreCompleto, String email, 
                        String especialidad, String descripcion, String telefono,
                        LocalDate fechaRegistro) {
        this.idCoach = idCoach;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.especialidad = especialidad;
        this.descripcion = descripcion;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters and Setters

    public Long getIdCoach() {
        return idCoach;
    }

    public void setIdCoach(Long idCoach) {
        this.idCoach = idCoach;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
