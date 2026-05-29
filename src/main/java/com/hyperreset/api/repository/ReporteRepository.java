package com.hyperreset.api.repository;

import com.hyperreset.api.entity.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    @Query("SELECT r FROM Reporte r JOIN FETCH r.testFisico t JOIN FETCH t.deportista d JOIN FETCH d.usuario " +
           "WHERE r.idReporte = :id")
    Optional<Reporte> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT r FROM Reporte r JOIN FETCH r.testFisico t JOIN FETCH t.deportista d JOIN FETCH d.usuario " +
           "WHERE t.deportista.idDeportista = :deportistaId")
    List<Reporte> findByDeportistaId(@Param("deportistaId") Long deportistaId);

    @Query("SELECT r FROM Reporte r JOIN FETCH r.testFisico t JOIN FETCH t.deportista d JOIN FETCH d.usuario " +
           "WHERE t.idTestFisico = :testFisicoId")
    List<Reporte> findByTestFisicoId(@Param("testFisicoId") Long testFisicoId);

    Optional<Reporte> findByTestFisicoIdTestFisico(Long idTestFisico);
}
