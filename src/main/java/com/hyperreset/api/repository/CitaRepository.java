package com.hyperreset.api.repository;

import com.hyperreset.api.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    @Query("SELECT c FROM Cita c JOIN FETCH c.deportista d JOIN FETCH d.usuario " +
           "JOIN FETCH c.coach co JOIN FETCH co.usuario WHERE c.coach.idCoach = :coachId")
    List<Cita> findByCoachId(@Param("coachId") Long coachId);

    @Query("SELECT c FROM Cita c JOIN FETCH c.deportista d JOIN FETCH d.usuario " +
           "JOIN FETCH c.coach co JOIN FETCH co.usuario WHERE c.deportista.idDeportista = :deportistaId")
    List<Cita> findByDeportistaId(@Param("deportistaId") Long deportistaId);

    @Query("SELECT c FROM Cita c JOIN FETCH c.deportista d JOIN FETCH d.usuario " +
           "JOIN FETCH c.coach co JOIN FETCH co.usuario WHERE c.idCita = :id")
    Optional<Cita> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT c FROM Cita c JOIN FETCH c.deportista d JOIN FETCH d.usuario " +
           "JOIN FETCH c.coach co JOIN FETCH co.usuario WHERE c.fechaHora BETWEEN :start AND :end")
    List<Cita> findByFechaHoraBetweenWithRelations(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    List<Cita> findByFechaHoraBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT c FROM Cita c JOIN FETCH c.deportista d JOIN FETCH d.usuario " +
           "JOIN FETCH c.coach co JOIN FETCH co.usuario")
    List<Cita> findAllWithRelations();
}
