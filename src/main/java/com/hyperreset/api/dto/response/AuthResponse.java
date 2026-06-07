package com.hyperreset.api.dto.response;

import java.time.LocalDate;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String rol;
    private String nombre;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private Long deportistaId;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long userId, String email, String rol, String nombre) {
        this.token = token;
        this.type = "Bearer";
        this.userId = userId;
        this.email = email;
        this.rol = rol;
        this.nombre = nombre;
    }

    public AuthResponse(String token, Long userId, String email, String rol, String nombre,
                        Long deportistaId) {
        this.token = token;
        this.type = "Bearer";
        this.userId = userId;
        this.email = email;
        this.rol = rol;
        this.nombre = nombre;
        this.deportistaId = deportistaId;
    }

    public AuthResponse(String token, Long userId, String email, String rol, String nombre,
                        String telefono, String direccion, LocalDate fechaNacimiento,
                        Long deportistaId) {
        this.token = token;
        this.type = "Bearer";
        this.userId = userId;
        this.email = email;
        this.rol = rol;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
        this.deportistaId = deportistaId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getDeportistaId() {
        return deportistaId;
    }

    public void setDeportistaId(Long deportistaId) {
        this.deportistaId = deportistaId;
    }
}
