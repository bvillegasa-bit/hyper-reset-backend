package com.hyperreset.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reporte")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Long idReporte;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_test_fisico", nullable = false, unique = true)
    private TestFisico testFisico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_coach", nullable = false)
    private Coach coach;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "observaciones_reporte", columnDefinition = "TEXT")
    private String observacionesReporte;

    @Column(name = "recomendaciones_reporte", columnDefinition = "TEXT")
    private String recomendacionesReporte;

    @Column(name = "ruta_pdf_reporte", length = 500)
    private String rutaPdfReporte;

    public Reporte() {
    }

    public Reporte(TestFisico testFisico, Coach coach) {
        this.testFisico = testFisico;
        this.coach = coach;
        this.fechaGeneracion = LocalDateTime.now();
    }

    @PrePersist
    protected void prePersist() {
        if (fechaGeneracion == null) {
            fechaGeneracion = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Long idReporte) {
        this.idReporte = idReporte;
    }

    public TestFisico getTestFisico() {
        return testFisico;
    }

    public void setTestFisico(TestFisico testFisico) {
        this.testFisico = testFisico;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getObservacionesReporte() {
        return observacionesReporte;
    }

    public void setObservacionesReporte(String observacionesReporte) {
        this.observacionesReporte = observacionesReporte;
    }

    public String getRecomendacionesReporte() {
        return recomendacionesReporte;
    }

    public void setRecomendacionesReporte(String recomendacionesReporte) {
        this.recomendacionesReporte = recomendacionesReporte;
    }

    public String getRutaPdfReporte() {
        return rutaPdfReporte;
    }

    public void setRutaPdfReporte(String rutaPdfReporte) {
        this.rutaPdfReporte = rutaPdfReporte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reporte reporte = (Reporte) o;
        return Objects.equals(idReporte, reporte.idReporte);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReporte);
    }

    @Override
    public String toString() {
        return "Reporte{" +
                "idReporte=" + idReporte +
                ", fechaGeneracion=" + fechaGeneracion +
                '}';
    }
}
