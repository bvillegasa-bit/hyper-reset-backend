package com.hyperreset.api.entity;

import com.hyperreset.api.entity.enums.Calificacion;
import com.hyperreset.api.entity.enums.TipoTest;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "resultado_test")
public class ResultadoTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado")
    private Long idResultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_test_fisico", nullable = false)
    private TestFisico testFisico;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_test", nullable = false, length = 30)
    private TipoTest tipoTest;

    @Column(name = "valor_obtenido", nullable = false, precision = 8, scale = 3)
    private BigDecimal valorObtenido;

    @Column(name = "unidad_medicion", nullable = false, length = 20)
    private String unidadMedicion;

    @Enumerated(EnumType.STRING)
    @Column(name = "calificacion", length = 20)
    private Calificacion calificacion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    public ResultadoTest() {
    }

    public ResultadoTest(TestFisico testFisico, TipoTest tipoTest, BigDecimal valorObtenido, String unidadMedicion) {
        this.testFisico = testFisico;
        this.tipoTest = tipoTest;
        this.valorObtenido = valorObtenido;
        this.unidadMedicion = unidadMedicion;
    }

    // Getters and Setters

    public Long getIdResultado() {
        return idResultado;
    }

    public void setIdResultado(Long idResultado) {
        this.idResultado = idResultado;
    }

    public TestFisico getTestFisico() {
        return testFisico;
    }

    public void setTestFisico(TestFisico testFisico) {
        this.testFisico = testFisico;
    }

    public TipoTest getTipoTest() {
        return tipoTest;
    }

    public void setTipoTest(TipoTest tipoTest) {
        this.tipoTest = tipoTest;
    }

    public BigDecimal getValorObtenido() {
        return valorObtenido;
    }

    public void setValorObtenido(BigDecimal valorObtenido) {
        this.valorObtenido = valorObtenido;
    }

    public String getUnidadMedicion() {
        return unidadMedicion;
    }

    public void setUnidadMedicion(String unidadMedicion) {
        this.unidadMedicion = unidadMedicion;
    }

    public Calificacion getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Calificacion calificacion) {
        this.calificacion = calificacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultadoTest that = (ResultadoTest) o;
        return Objects.equals(idResultado, that.idResultado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idResultado);
    }

    @Override
    public String toString() {
        return "ResultadoTest{" +
                "idResultado=" + idResultado +
                ", tipoTest=" + tipoTest +
                ", valorObtenido=" + valorObtenido +
                ", unidadMedicion='" + unidadMedicion + '\'' +
                '}';
    }
}
