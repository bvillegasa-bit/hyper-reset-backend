package com.hyperreset.api.dto.response;

import java.util.List;

/**
 * DTO for the coach dashboard endpoint GET /api/dashboard/coach/{id}.
 */
public class DashboardCoachResponse {

    private EstadisticasInfo estadisticas;
    private List<ActividadRecienteItem> actividadReciente;
    private List<ProximaCitaCoachItem> proximasCitas;

    public DashboardCoachResponse() {
    }

    public EstadisticasInfo getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(EstadisticasInfo estadisticas) {
        this.estadisticas = estadisticas;
    }

    public List<ActividadRecienteItem> getActividadReciente() {
        return actividadReciente;
    }

    public void setActividadReciente(List<ActividadRecienteItem> actividadReciente) {
        this.actividadReciente = actividadReciente;
    }

    public List<ProximaCitaCoachItem> getProximasCitas() {
        return proximasCitas;
    }

    public void setProximasCitas(List<ProximaCitaCoachItem> proximasCitas) {
        this.proximasCitas = proximasCitas;
    }

    // ==================================================================
    // Inner DTOs
    // ==================================================================

    public static class EstadisticasInfo {
        private int pacientesHoy;
        private int pruebasPendientes;
        private int reportes;

        public EstadisticasInfo() {
        }

        public EstadisticasInfo(int pacientesHoy, int pruebasPendientes, int reportes) {
            this.pacientesHoy = pacientesHoy;
            this.pruebasPendientes = pruebasPendientes;
            this.reportes = reportes;
        }

        public int getPacientesHoy() { return pacientesHoy; }
        public void setPacientesHoy(int pacientesHoy) { this.pacientesHoy = pacientesHoy; }
        public int getPruebasPendientes() { return pruebasPendientes; }
        public void setPruebasPendientes(int pruebasPendientes) { this.pruebasPendientes = pruebasPendientes; }
        public int getReportes() { return reportes; }
        public void setReportes(int reportes) { this.reportes = reportes; }
    }

    public static class ActividadRecienteItem {
        private String paciente;
        private String accion;
        private String timestamp;
        private String tipo;

        public ActividadRecienteItem() {
        }

        public ActividadRecienteItem(String paciente, String accion, String timestamp, String tipo) {
            this.paciente = paciente;
            this.accion = accion;
            this.timestamp = timestamp;
            this.tipo = tipo;
        }

        public String getPaciente() { return paciente; }
        public void setPaciente(String paciente) { this.paciente = paciente; }
        public String getAccion() { return accion; }
        public void setAccion(String accion) { this.accion = accion; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
    }

    public static class ProximaCitaCoachItem {
        private String deportistaNombre;
        private String fecha;
        private String hora;
        private String motivo;

        public ProximaCitaCoachItem() {
        }

        public ProximaCitaCoachItem(String deportistaNombre, String fecha, String hora, String motivo) {
            this.deportistaNombre = deportistaNombre;
            this.fecha = fecha;
            this.hora = hora;
            this.motivo = motivo;
        }

        public String getDeportistaNombre() { return deportistaNombre; }
        public void setDeportistaNombre(String deportistaNombre) { this.deportistaNombre = deportistaNombre; }
        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }
        public String getHora() { return hora; }
        public void setHora(String hora) { this.hora = hora; }
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }
}
