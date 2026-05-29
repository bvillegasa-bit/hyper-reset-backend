package com.hyperreset.api.entity;

import com.hyperreset.api.entity.enums.TipoMensaje;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "mensaje")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Long idMensaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_remitente", nullable = false)
    private Usuario remitente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destinatario", nullable = false)
    private Usuario destinatario;

    @Column(name = "contenido_mmensaje", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mensaje", nullable = false, length = 20)
    private TipoMensaje tipoMensaje = TipoMensaje.TEXTO;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "estado_leido", nullable = false)
    private Boolean estadoLeido = false;

    public Mensaje() {
    }

    public Mensaje(Usuario remitente, Usuario destinatario, String contenido) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.contenido = contenido;
        this.tipoMensaje = TipoMensaje.TEXTO;
        this.fechaEnvio = LocalDateTime.now();
        this.estadoLeido = false;
    }

    @PrePersist
    protected void prePersist() {
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }
        if (estadoLeido == null) {
            estadoLeido = false;
        }
        if (tipoMensaje == null) {
            tipoMensaje = TipoMensaje.TEXTO;
        }
    }

    // Getters and Setters

    public Long getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(Long idMensaje) {
        this.idMensaje = idMensaje;
    }

    public Usuario getRemitente() {
        return remitente;
    }

    public void setRemitente(Usuario remitente) {
        this.remitente = remitente;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Usuario destinatario) {
        this.destinatario = destinatario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public TipoMensaje getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(TipoMensaje tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Boolean getEstadoLeido() {
        return estadoLeido;
    }

    public void setEstadoLeido(Boolean estadoLeido) {
        this.estadoLeido = estadoLeido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mensaje mensaje = (Mensaje) o;
        return Objects.equals(idMensaje, mensaje.idMensaje);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMensaje);
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "idMensaje=" + idMensaje +
                ", tipoMensaje=" + tipoMensaje +
                ", fechaEnvio=" + fechaEnvio +
                ", estadoLeido=" + estadoLeido +
                '}';
    }
}
