package com.hyperreset.api.repository;

import com.hyperreset.api.entity.Deportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeportistaRepository extends JpaRepository<Deportista, Long> {

    List<Deportista> findByCoachIdCoach(Long coachId);

    @Query("SELECT d FROM Deportista d JOIN FETCH d.usuario WHERE d.idDeportista = :id")
    Optional<Deportista> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT d FROM Deportista d JOIN FETCH d.usuario WHERE d.coach.idCoach = :coachId")
    List<Deportista> findByCoachIdWithUser(@Param("coachId") Long coachId);

    @Query("SELECT d FROM Deportista d JOIN FETCH d.usuario WHERE d.usuario.idUsuario = :usuarioId")
    Optional<Deportista> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
