package com.hyperreset.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * DTO for the deportista dashboard endpoint GET /api/dashboard/deportista/{id}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardDeportistaResponse {

    private ProximaCitaInfo proximaCita;
    private List<ProgresoSemanalItem> progresoSemanal;
    private MetaDelMesInfo metaDelMes;
    private LogrosInfo logros;

    public DashboardDeportistaResponse() {
    }

    public ProximaCitaInfo getProximaCita() {
        return proximaCita;
    }

    public void setProximaCita(ProximaCitaInfo proximaCita) {
        this.proximaCita = proximaCita;
    }

    public List<ProgresoSemanalItem> getProgresoSemanal() {
        return progresoSemanal;
    }

    public void setProgresoSemanal(List<ProgresoSemanalItem> progresoSemanal) {
        this.progresoSemanal = progresoSemanal;
    }

    public MetaDelMesInfo getMetaDelMes() {
        return metaDelMes;
    }

    public void setMetaDelMes(MetaDelMesInfo metaDelMes) {
        this.metaDelMes = metaDelMes;
    }

    public LogrosInfo getLogros() {
        return logros;
    }

    public void setLogros(LogrosInfo logros) {
        this.logros = logros;
    }

    // ==================================================================
    // Inner DTOs
    // ==================================================================

    public static class ProximaCitaInfo {
        private String fecha;
        private String hora;
        private String coachNombre;
        private String motivo;

        public ProximaCitaInfo() {
        }

        public ProximaCitaInfo(String fecha, String hora, String coachNombre, String motivo) {
            this.fecha = fecha;
            this.hora = hora;
            this.coachNombre = coachNombre;
            this.motivo = motivo;
        }

        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }
        public String getHora() { return hora; }
        public void setHora(String hora) { this.hora = hora; }
        public String getCoachNombre() { return coachNombre; }
        public void setCoachNombre(String coachNombre) { this.coachNombre = coachNombre; }
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }

    public static class ProgresoSemanalItem {
        private String dia;
        private int valor;

        public ProgresoSemanalItem() {
        }

        public ProgresoSemanalItem(String dia, int valor) {
            this.dia = dia;
            this.valor = valor;
        }

        public String getDia() { return dia; }
        public void setDia(String dia) { this.dia = dia; }
        public int getValor() { return valor; }
        public void setValor(int valor) { this.valor = valor; }
    }

    public static class MetaDelMesInfo {
        private String descripcion;
        private int actual;
        private int objetivo;

        public MetaDelMesInfo() {
        }

        public MetaDelMesInfo(String descripcion, int actual, int objetivo) {
            this.descripcion = descripcion;
            this.actual = actual;
            this.objetivo = objetivo;
        }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public int getActual() { return actual; }
        public void setActual(int actual) { this.actual = actual; }
        public int getObjetivo() { return objetivo; }
        public void setObjetivo(int objetivo) { this.objetivo = objetivo; }
    }

    public static class LogrosInfo {
        private int testsCompletados;
        private int totalTests;
        private int sesiones;
        private int racha;

        public LogrosInfo() {
        }

        public LogrosInfo(int testsCompletados, int totalTests, int sesiones, int racha) {
            this.testsCompletados = testsCompletados;
            this.totalTests = totalTests;
            this.sesiones = sesiones;
            this.racha = racha;
        }

        public int getTestsCompletados() { return testsCompletados; }
        public void setTestsCompletados(int testsCompletados) { this.testsCompletados = testsCompletados; }
        public int getTotalTests() { return totalTests; }
        public void setTotalTests(int totalTests) { this.totalTests = totalTests; }
        public int getSesiones() { return sesiones; }
        public void setSesiones(int sesiones) { this.sesiones = sesiones; }
        public int getRacha() { return racha; }
        public void setRacha(int racha) { this.racha = racha; }
    }
}
