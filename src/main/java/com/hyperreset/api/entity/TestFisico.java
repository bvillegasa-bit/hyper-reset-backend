package com.hyperreset.api.entity;

import com.hyperreset.api.entity.enums.EstadoTest;
import com.hyperreset.api.entity.enums.TipoTest;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "test_fisico")
public class TestFisico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_test_fisico")
    private Long idTestFisico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_deportista", nullable = false)
    private Deportista deportista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_coach", nullable = true)
    private Coach coach;

    @Column(name = "fecha_ejecucion", nullable = false)
    private LocalDateTime fechaEjecucion;

    @Column(name = "lugar", length = 150)
    private String lugar;

    @Column(name = "peso_deportista", precision = 5, scale = 2)
    private BigDecimal pesoDeportista;

    @Column(name = "estatura_parado_deportista", precision = 5, scale = 2)
    private BigDecimal estaturaParadoDeportista;

    @Column(name = "estatura_sentado_deportista", precision = 5, scale = 2)
    private BigDecimal estaturaSentadoDeportista;

    @Column(name = "antecedentes_medicos", columnDefinition = "TEXT")
    private String antecedentesMedicos;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_test", nullable = false, length = 30)
    private TipoTest tipoTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_test", nullable = false, length = 20)
    private EstadoTest estadoTest = EstadoTest.EN_PROGRESO;

    @OneToMany(mappedBy = "testFisico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ResultadoTest> resultados = new ArrayList<>();

    @OneToOne(mappedBy = "testFisico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Reporte reporte;

    public TestFisico() {
    }

    public TestFisico(Deportista deportista, Coach coach, TipoTest tipoTest) {
        this.deportista = deportista;
        this.coach = coach;
        this.tipoTest = tipoTest;
        this.fechaEjecucion = LocalDateTime.now();
        this.estadoTest = EstadoTest.EN_PROGRESO;
    }

    public TestFisico(Deportista deportista, Coach coach) {
        this.deportista = deportista;
        this.coach = coach;
        this.fechaEjecucion = LocalDateTime.now();
        this.estadoTest = EstadoTest.EN_PROGRESO;
    }

    @PrePersist
    protected void prePersist() {
        if (fechaEjecucion == null) {
            fechaEjecucion = LocalDateTime.now();
        }
        if (estadoTest == null) {
            estadoTest = EstadoTest.EN_PROGRESO;
        }
    }

    // Getters and Setters

    public Long getIdTestFisico() {
        return idTestFisico;
    }

    public void setIdTestFisico(Long idTestFisico) {
        this.idTestFisico = idTestFisico;
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

    public LocalDateTime getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(LocalDateTime fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public BigDecimal getPesoDeportista() {
        return pesoDeportista;
    }

    public void setPesoDeportista(BigDecimal pesoDeportista) {
        this.pesoDeportista = pesoDeportista;
    }

    public BigDecimal getEstaturaParadoDeportista() {
        return estaturaParadoDeportista;
    }

    public void setEstaturaParadoDeportista(BigDecimal estaturaParadoDeportista) {
        this.estaturaParadoDeportista = estaturaParadoDeportista;
    }

    public BigDecimal getEstaturaSentadoDeportista() {
        return estaturaSentadoDeportista;
    }

    public void setEstaturaSentadoDeportista(BigDecimal estaturaSentadoDeportista) {
        this.estaturaSentadoDeportista = estaturaSentadoDeportista;
    }

    public String getAntecedentesMedicos() {
        return antecedentesMedicos;
    }

    public void setAntecedentesMedicos(String antecedentesMedicos) {
        this.antecedentesMedicos = antecedentesMedicos;
    }

    public TipoTest getTipoTest() {
        return tipoTest;
    }

    public void setTipoTest(TipoTest tipoTest) {
        this.tipoTest = tipoTest;
    }

    public EstadoTest getEstadoTest() {
        return estadoTest;
    }

    public void setEstadoTest(EstadoTest estadoTest) {
        this.estadoTest = estadoTest;
    }

    public List<ResultadoTest> getResultados() {
        return resultados;
    }

    public void setResultados(List<ResultadoTest> resultados) {
        this.resultados = resultados;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestFisico that = (TestFisico) o;
        return Objects.equals(idTestFisico, that.idTestFisico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTestFisico);
    }

    @Override
    public String toString() {
        return "TestFisico{" +
                "idTestFisico=" + idTestFisico +
                ", fechaEjecucion=" + fechaEjecucion +
                ", estadoTest=" + estadoTest +
                '}';
    }
}
