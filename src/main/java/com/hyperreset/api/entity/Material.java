package com.hyperreset.api.entity;

import com.hyperreset.api.entity.enums.TipoMaterial;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "material")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_material")
    private Long idMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_coach", nullable = false)
    private Coach coach;

    @Column(name = "titulo_material", nullable = false, length = 200)
    private String tituloMaterial;

    @Column(name = "descripcion_material", columnDefinition = "TEXT")
    private String descripcionMaterial;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_material", nullable = false, length = 20)
    private TipoMaterial tipoMaterial;

    @Column(name = "url_contenido", nullable = false, length = 500)
    private String urlContenido;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    public Material() {
    }

    public Material(Coach coach, String tituloMaterial, TipoMaterial tipoMaterial, String urlContenido) {
        this.coach = coach;
        this.tituloMaterial = tituloMaterial;
        this.tipoMaterial = tipoMaterial;
        this.urlContenido = urlContenido;
        this.fechaSubida = LocalDateTime.now();
    }

    @PrePersist
    protected void prePersist() {
        if (fechaSubida == null) {
            fechaSubida = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public String getTituloMaterial() {
        return tituloMaterial;
    }

    public void setTituloMaterial(String tituloMaterial) {
        this.tituloMaterial = tituloMaterial;
    }

    public String getDescripcionMaterial() {
        return descripcionMaterial;
    }

    public void setDescripcionMaterial(String descripcionMaterial) {
        this.descripcionMaterial = descripcionMaterial;
    }

    public TipoMaterial getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(TipoMaterial tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public String getUrlContenido() {
        return urlContenido;
    }

    public void setUrlContenido(String urlContenido) {
        this.urlContenido = urlContenido;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return Objects.equals(idMaterial, material.idMaterial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMaterial);
    }

    @Override
    public String toString() {
        return "Material{" +
                "idMaterial=" + idMaterial +
                ", tituloMaterial='" + tituloMaterial + '\'' +
                ", tipoMaterial=" + tipoMaterial +
                '}';
    }
}
