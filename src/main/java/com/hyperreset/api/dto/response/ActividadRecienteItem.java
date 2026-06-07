package com.hyperreset.api.dto.response;

/**
 * DTO representing a single recent activity item.
 * Used by the paginated activity endpoint and the coach dashboard.
 */
public class ActividadRecienteItem {

    private String pacienteNombre;
    private String accion;
    private String timestamp;
    private String tipo;

    public ActividadRecienteItem() {
    }

    public ActividadRecienteItem(String pacienteNombre, String accion, String timestamp, String tipo) {
        this.pacienteNombre = pacienteNombre;
        this.accion = accion;
        this.timestamp = timestamp;
        this.tipo = tipo;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public void setPacienteNombre(String pacienteNombre) {
        this.pacienteNombre = pacienteNombre;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
