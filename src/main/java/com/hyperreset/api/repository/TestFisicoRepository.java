package com.hyperreset.api.repository;

import com.hyperreset.api.entity.TestFisico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestFisicoRepository extends JpaRepository<TestFisico, Long> {

    @Query("SELECT t FROM TestFisico t JOIN FETCH t.deportista d JOIN FETCH d.usuario " +
           "JOIN FETCH t.coach c JOIN FETCH c.usuario WHERE t.deportista.idDeportista = :deportistaId")
    List<TestFisico> findByDeportistaId(@Param("deportistaId") Long deportistaId);

    @Query("SELECT t FROM TestFisico t JOIN FETCH t.deportista d JOIN FETCH d.usuario " +
           "JOIN FETCH t.coach c JOIN FETCH c.usuario WHERE t.idTestFisico = :id")
    Optional<TestFisico> findByIdWithDeportista(@Param("id") Long id);
}
