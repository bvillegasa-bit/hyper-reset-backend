package com.hyperreset.api.repository;

import com.hyperreset.api.entity.ResultadoTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResultadoTestRepository extends JpaRepository<ResultadoTest, Long> {

    List<ResultadoTest> findByTestFisicoIdTestFisico(Long idTestFisico);

    @Query("SELECT r FROM ResultadoTest r JOIN r.testFisico t WHERE t.deportista.idDeportista = :deportistaId")
    List<ResultadoTest> findByTestFisicoDeportistaId(@Param("deportistaId") Long deportistaId);

    @Query("SELECT r FROM ResultadoTest r JOIN FETCH r.testFisico t " +
           "WHERE t.deportista.idDeportista = :deportistaId " +
           "AND t.fechaEjecucion BETWEEN :start AND :end " +
           "ORDER BY t.fechaEjecucion ASC")
    List<ResultadoTest> findByDeportistaIdAndFechaBetween(@Param("deportistaId") Long deportistaId,
                                                           @Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end);
}
