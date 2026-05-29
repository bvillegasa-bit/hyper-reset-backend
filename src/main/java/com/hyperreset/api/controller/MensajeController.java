package com.hyperreset.api.controller;

import com.hyperreset.api.dto.request.MensajeRequest;
import com.hyperreset.api.dto.response.ApiResponse;
import com.hyperreset.api.dto.response.MensajeResponse;
import com.hyperreset.api.security.CurrentUser;
import com.hyperreset.api.service.MensajeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    private static final Logger log = LoggerFactory.getLogger(MensajeController.class);

    @Autowired
    private MensajeService mensajeService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MensajeResponse>> sendMessage(
            @Valid @RequestBody MensajeRequest request,
            @CurrentUser Long userId) {
        log.info("POST /api/mensajes - sending message from userId: {}", userId);
        MensajeResponse mensaje = mensajeService.sendMessage(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent successfully", mensaje));
    }

    @GetMapping("/conversacion")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<MensajeResponse>>> getConversacion(
            @RequestParam("with") Long otherUserId,
            @CurrentUser Long userId) {
        log.info("GET /api/mensajes/conversacion?with={}", otherUserId);
        List<MensajeResponse> mensajes = mensajeService.getConversacion(userId, otherUserId);
        return ResponseEntity.ok(ApiResponse.success(mensajes));
    }

    @GetMapping("/recibidos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<MensajeResponse>>> getMensajesRecibidos(
            @CurrentUser Long userId) {
        log.info("GET /api/mensajes/recibidos for userId: {}", userId);
        List<MensajeResponse> mensajes = mensajeService.getMensajesRecibidos(userId);
        return ResponseEntity.ok(ApiResponse.success(mensajes));
    }

    @GetMapping("/enviados")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<MensajeResponse>>> getMensajesEnviados(
            @CurrentUser Long userId) {
        log.info("GET /api/mensajes/enviados for userId: {}", userId);
        List<MensajeResponse> mensajes = mensajeService.getMensajesEnviados(userId);
        return ResponseEntity.ok(ApiResponse.success(mensajes));
    }

    @PatchMapping("/{id}/leer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> marcarComoLeido(@PathVariable Long id) {
        log.info("PATCH /api/mensajes/{}/leer", id);
        mensajeService.marcarComoLeido(id);
        return ResponseEntity.ok(ApiResponse.success("Message marked as read", null));
    }

    @GetMapping("/no-leidos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Integer>> getNoLeidosCount(@CurrentUser Long userId) {
        log.info("GET /api/mensajes/no-leidos for userId: {}", userId);
        int count = mensajeService.getNoLeidosCount(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
