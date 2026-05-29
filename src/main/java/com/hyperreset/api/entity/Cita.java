package com.hyperreset.api.entity;

import com.hyperreset.api.entity.enums.EstadoCita;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Long idCita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_deportista", nullable = false)
    private Deportista deportista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_coach", nullable = false)
    private Coach coach;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "tipo_sesion", nullable = false, length = 100)
    private String tipoSesion;

    @Column(name = "duracion_sesion", nullable = false)
    private Integer duracionSesion = 60;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.PENDIENTE;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    public Cita() {
    }

    public Cita(Deportista deportista, Coach coach, LocalDateTime fechaHora, String tipoSesion) {
        this.deportista = deportista;
        this.coach = coach;
        this.fechaHora = fechaHora;
        this.tipoSesion = tipoSesion;
        this.duracionSesion = 60;
        this.estado = EstadoCita.PENDIENTE;
    }

    // Getters and Setters

    public Long getIdCita() {
        return idCita;
    }

    public void setIdCita(Long idCita) {
        this.idCita = idCita;
    }

    public Deportista getDeportista() {
        return deportista;
    }

    public void setDeportista(Deportista deportista) {
        this.deportista = deportista;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipoSesion() {
        return tipoSesion;
    }

    public void setTipoSesion(String tipoSesion) {
        this.tipoSesion = tipoSesion;
    }

    public Integer getDuracionSesion() {
        return duracionSesion;
    }

    public void setDuracionSesion(Integer duracionSesion) {
        this.duracionSesion = duracionSesion;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cita cita = (Cita) o;
        return Objects.equals(idCita, cita.idCita);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCita);
    }

    @Override
    public String toString() {
        return "Cita{" +
                "idCita=" + idCita +
                ", fechaHora=" + fechaHora +
                ", tipoSesion='" + tipoSesion + '\'' +
                ", estado=" + estado +
                '}';
    }
}
