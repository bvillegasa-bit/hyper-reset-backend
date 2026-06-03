package com.hyperreset.api.dto.response;

import java.time.LocalDate;

public class DeportistaResponse {

    private Long id;
    private Long usuarioId;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String coachNombre;
    private LocalDate fechaRegistro;
    private String direccion;
    private String tempPassword;

    public DeportistaResponse() {
    }

    public DeportistaResponse(Long id, Long usuarioId, String nombreCompleto, String email, String telefono,
                              LocalDate fechaNacimiento, String coachNombre, LocalDate fechaRegistro,
                              String direccion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.coachNombre = coachNombre;
        this.fechaRegistro = fechaRegistro;
        this.direccion = direccion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCoachNombre() {
        return coachNombre;
    }

    public void setCoachNombre(String coachNombre) {
        this.coachNombre = coachNombre;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

}
