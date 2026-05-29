package com.hyperreset.api.service;

import com.hyperreset.api.dto.request.MensajeRequest;
import com.hyperreset.api.dto.response.MensajeResponse;
import com.hyperreset.api.entity.Mensaje;
import com.hyperreset.api.entity.Usuario;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.MensajeRepository;
import com.hyperreset.api.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MensajeService {

    private static final Logger log = LoggerFactory.getLogger(MensajeService.class);

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public MensajeResponse sendMessage(Long remitenteId, MensajeRequest request) {
        log.debug("Sending message from userId: {} to userId: {}", remitenteId, request.getDestinatarioId());

        Usuario remitente = usuarioRepository.findById(remitenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", remitenteId));

        Usuario destinatario = usuarioRepository.findById(request.getDestinatarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.getDestinatarioId()));

        Mensaje mensaje = new Mensaje(remitente, destinatario, request.getContenido());
        Mensaje saved = mensajeRepository.save(mensaje);
        log.info("Message sent with id: {}", saved.getIdMensaje());

        return toMensajeResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MensajeResponse> getConversacion(Long usuario1Id, Long usuario2Id) {
        log.debug("Fetching conversation between userId: {} and userId: {}", usuario1Id, usuario2Id);
        return mensajeRepository.findConversacion(usuario1Id, usuario2Id).stream()
                .map(this::toMensajeResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MensajeResponse> getMensajesRecibidos(Long destinatarioId) {
        log.debug("Fetching received messages for userId: {}", destinatarioId);
        return mensajeRepository.findByDestinatarioId(destinatarioId).stream()
                .map(this::toMensajeResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MensajeResponse> getMensajesEnviados(Long remitenteId) {
        log.debug("Fetching sent messages for userId: {}", remitenteId);
        return mensajeRepository.findByRemitenteId(remitenteId).stream()
                .map(this::toMensajeResponse)
                .collect(Collectors.toList());
    }

    public void marcarComoLeido(Long mensajeId) {
        log.debug("Marking message as read, id: {}", mensajeId);
        Mensaje mensaje = mensajeRepository.findById(mensajeId)
                .orElseThrow(() -> new ResourceNotFoundException("Mensaje", "id", mensajeId));
        mensaje.setEstadoLeido(true);
        mensajeRepository.save(mensaje);
        log.info("Message id: {} marked as read", mensajeId);
    }

    @Transactional(readOnly = true)
    public int getNoLeidosCount(Long userId) {
        return mensajeRepository.countNoLeidos(userId);
    }

    private MensajeResponse toMensajeResponse(Mensaje mensaje) {
        MensajeResponse response = new MensajeResponse();
        response.setId(mensaje.getIdMensaje());
        response.setRemitenteId(mensaje.getRemitente().getIdUsuario());

        String remitenteNombre = mensaje.getRemitente().getNombres()
                + " " + mensaje.getRemitente().getApellidos();
        response.setRemitenteNombre(remitenteNombre);

        response.setDestinatarioId(mensaje.getDestinatario().getIdUsuario());
        response.setContenido(mensaje.getContenido());
        response.setFechaEnvio(mensaje.getFechaEnvio());
        response.setLeido(mensaje.getEstadoLeido() != null && mensaje.getEstadoLeido());

        return response;
    }
}
