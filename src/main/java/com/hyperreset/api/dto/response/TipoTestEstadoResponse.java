package com.hyperreset.api.dto.response;

/**
 * DTO that represents one of the 8 fixed test types with its completion status
 * for a specific deportista. Used by GET /api/resultados/tipos-con-estado/{deportistaId}.
 */
public class TipoTestEstadoResponse {

    private String tipoTest;
    private String nombre;
    private String icono;
    private String duracion;
    private boolean completado;
    private Double ultimoValor;
    private String unidad;
    private String fechaUltimo;

    public TipoTestEstadoResponse() {
    }

    public TipoTestEstadoResponse(String tipoTest, String nombre, String icono, String duracion,
                                  boolean completado, Double ultimoValor, String unidad, String fechaUltimo) {
        this.tipoTest = tipoTest;
        this.nombre = nombre;
        this.icono = icono;
        this.duracion = duracion;
        this.completado = completado;
        this.ultimoValor = ultimoValor;
        this.unidad = unidad;
        this.fechaUltimo = fechaUltimo;
    }

    // Getters and Setters

    public String getTipoTest() {
        return tipoTest;
    }

    public void setTipoTest(String tipoTest) {
        this.tipoTest = tipoTest;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public Double getUltimoValor() {
        return ultimoValor;
    }

    public void setUltimoValor(Double ultimoValor) {
        this.ultimoValor = ultimoValor;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getFechaUltimo() {
        return fechaUltimo;
    }

    public void setFechaUltimo(String fechaUltimo) {
        this.fechaUltimo = fechaUltimo;
    }
}
