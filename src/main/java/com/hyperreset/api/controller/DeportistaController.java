package com.hyperreset.api.controller;

import com.hyperreset.api.dto.request.DeportistaRequest;
import com.hyperreset.api.dto.response.ApiResponse;
import com.hyperreset.api.dto.response.DeportistaResponse;
import com.hyperreset.api.service.DeportistaService;
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
@RequestMapping("/api/deportistas")
public class DeportistaController {

    private static final Logger log = LoggerFactory.getLogger(DeportistaController.class);

    @Autowired
    private DeportistaService deportistaService;

    @GetMapping
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DeportistaResponse>>> getAllDeportistas() {
        log.debug("GET /api/deportistas");
        List<DeportistaResponse> deportistas = deportistaService.getAllDeportistas();
        return ResponseEntity.ok(ApiResponse.success(deportistas));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN') or hasRole('DEPORTISTA')")
    public ResponseEntity<ApiResponse<DeportistaResponse>> getDeportistaById(@PathVariable Long id) {
        log.debug("GET /api/deportistas/{}", id);
        DeportistaResponse deportista = deportistaService.getDeportistaById(id);
        return ResponseEntity.ok(ApiResponse.success(deportista));
    }

    @GetMapping("/coach/{coachId}")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DeportistaResponse>>> getDeportistasByCoach(@PathVariable Long coachId) {
        log.debug("GET /api/deportistas/coach/{}", coachId);
        List<DeportistaResponse> deportistas = deportistaService.getDeportistasByCoach(coachId);
        return ResponseEntity.ok(ApiResponse.success(deportistas));
    }

    @PostMapping
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeportistaResponse>> createDeportista(
            @Valid @RequestBody DeportistaRequest request) {
        log.debug("POST /api/deportistas");
        DeportistaResponse deportista = deportistaService.createDeportista(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deportista creado exitosamente", deportista));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeportistaResponse>> updateDeportista(
            @PathVariable Long id,
            @Valid @RequestBody DeportistaRequest request) {
        log.debug("PUT /api/deportistas/{}", id);
        DeportistaResponse deportista = deportistaService.updateDeportista(id, request);
        return ResponseEntity.ok(ApiResponse.success("Deportista actualizado exitosamente", deportista));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDeportista(@PathVariable Long id) {
        log.debug("DELETE /api/deportistas/{}", id);
        deportistaService.deleteDeportista(id);
        return ResponseEntity.ok(ApiResponse.success("Deportista eliminado exitosamente", null));
    }
}
