package com.hyperreset.api.entity;

import com.hyperreset.api.entity.enums.EstadoDeportista;
import com.hyperreset.api.entity.enums.NivelDeportista;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "deportista")
public class Deportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deportista")
    private Long idDeportista;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_coach")
    private Coach coach;

    @Column(name = "deporte", length = 100)
    private String deporte;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_deportista", length = 20)
    private NivelDeportista nivelDeportista;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_deportista", nullable = false, length = 20)
    private EstadoDeportista estadoDeportista = EstadoDeportista.PENDIENTE;

    @OneToMany(mappedBy = "deportista", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestFisico> testsFisicos = new ArrayList<>();

    @OneToMany(mappedBy = "deportista", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cita> citas = new ArrayList<>();

    public Deportista() {
    }

    public Deportista(Usuario usuario) {
        this.usuario = usuario;
        this.estadoDeportista = EstadoDeportista.PENDIENTE;
    }

    // Getters and Setters

    public Long getIdDeportista() {
        return idDeportista;
    }

    public void setIdDeportista(Long idDeportista) {
        this.idDeportista = idDeportista;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
    }

    public NivelDeportista getNivelDeportista() {
        return nivelDeportista;
    }

    public void setNivelDeportista(NivelDeportista nivelDeportista) {
        this.nivelDeportista = nivelDeportista;
    }

    public EstadoDeportista getEstadoDeportista() {
        return estadoDeportista;
    }

    public void setEstadoDeportista(EstadoDeportista estadoDeportista) {
        this.estadoDeportista = estadoDeportista;
    }

    public List<TestFisico> getTestsFisicos() {
        return testsFisicos;
    }

    public void setTestsFisicos(List<TestFisico> testsFisicos) {
        this.testsFisicos = testsFisicos;
    }

    public List<Cita> getCitas() {
        return citas;
    }

    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deportista that = (Deportista) o;
        return Objects.equals(idDeportista, that.idDeportista);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDeportista);
    }

    @Override
    public String toString() {
        return "Deportista{" +
                "idDeportista=" + idDeportista +
                ", deporte='" + deporte + '\'' +
                ", nivelDeportista=" + nivelDeportista +
                ", estadoDeportista=" + estadoDeportista +
                '}';
    }
}
