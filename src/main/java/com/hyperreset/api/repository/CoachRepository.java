package com.hyperreset.api.repository;

import com.hyperreset.api.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {

    @Query("SELECT c FROM Coach c WHERE c.usuario.idUsuario = :usuarioId")
    Optional<Coach> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    Optional<Coach> findByUsuario_IdUsuario(Long usuarioId);
}
