package com.hyperreset.api.repository;

import com.hyperreset.api.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("SELECT m FROM Mensaje m JOIN FETCH m.remitente JOIN FETCH m.destinatario " +
           "WHERE (m.remitente.idUsuario = :userId1 AND m.destinatario.idUsuario = :userId2) " +
           "OR (m.remitente.idUsuario = :userId2 AND m.destinatario.idUsuario = :userId1) " +
           "ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findConversacion(@Param("userId1") Long userId1,
                                    @Param("userId2") Long userId2);

    @Query("SELECT m FROM Mensaje m JOIN FETCH m.remitente JOIN FETCH m.destinatario " +
           "WHERE m.destinatario.idUsuario = :destinatarioId ORDER BY m.fechaEnvio DESC")
    List<Mensaje> findByDestinatarioId(@Param("destinatarioId") Long destinatarioId);

    @Query("SELECT m FROM Mensaje m JOIN FETCH m.remitente JOIN FETCH m.destinatario " +
           "WHERE m.remitente.idUsuario = :remitenteId ORDER BY m.fechaEnvio DESC")
    List<Mensaje> findByRemitenteId(@Param("remitenteId") Long remitenteId);

    @Query("SELECT m FROM Mensaje m WHERE m.destinatario.idUsuario = :destinatarioId AND m.estadoLeido = false " +
           "ORDER BY m.fechaEnvio DESC")
    List<Mensaje> findByDestinatarioIdAndEstadoLeidoFalse(@Param("destinatarioId") Long destinatarioId);

    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.destinatario.idUsuario = :userId AND m.estadoLeido = false")
    int countNoLeidos(@Param("userId") Long userId);
}
