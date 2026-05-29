package com.hyperreset.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public class DeportistaRequest {

    @NotBlank(message = "Nombres is required")
    private String nombres;

    @NotBlank(message = "Apellidos is required")
    private String apellidos;

    @Past(message = "Fecha de nacimiento must be in the past")
    private LocalDate fechaNacimiento;

    private String telefono;

    private String direccion;

    private Long coachId;

    public DeportistaRequest() {
    }

    public DeportistaRequest(String nombres, String apellidos, LocalDate fechaNacimiento,
                             String telefono, String direccion, Long coachId) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.direccion = direccion;
        this.coachId = coachId;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }
}
