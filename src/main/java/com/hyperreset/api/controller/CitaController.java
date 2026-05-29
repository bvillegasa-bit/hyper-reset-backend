package com.hyperreset.api.controller;

import com.hyperreset.api.dto.request.CitaRequest;
import com.hyperreset.api.dto.response.ApiResponse;
import com.hyperreset.api.dto.response.CitaResponse;
import com.hyperreset.api.entity.enums.EstadoCita;
import com.hyperreset.api.service.CitaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private static final Logger log = LoggerFactory.getLogger(CitaController.class);

    @Autowired
    private CitaService citaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CitaResponse>>> getAllCitas() {
        log.info("GET /api/citas - list all");
        List<CitaResponse> citas = citaService.getAllCitas();
        return ResponseEntity.ok(ApiResponse.success(citas));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN', 'DEPORTISTA')")
    public ResponseEntity<ApiResponse<CitaResponse>> getCitaById(@PathVariable Long id) {
        log.info("GET /api/citas/{}", id);
        CitaResponse cita = citaService.getCitaById(id);
        return ResponseEntity.ok(ApiResponse.success(cita));
    }

    @GetMapping("/coach/{coachId}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CitaResponse>>> getCitasByCoach(@PathVariable Long coachId) {
        log.info("GET /api/citas/coach/{}", coachId);
        List<CitaResponse> citas = citaService.getCitasByCoach(coachId);
        return ResponseEntity.ok(ApiResponse.success(citas));
    }

    @GetMapping("/deportista/{deportistaId}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN', 'DEPORTISTA')")
    public ResponseEntity<ApiResponse<List<CitaResponse>>> getCitasByDeportista(@PathVariable Long deportistaId) {
        log.info("GET /api/citas/deportista/{}", deportistaId);
        List<CitaResponse> citas = citaService.getCitasByDeportista(deportistaId);
        return ResponseEntity.ok(ApiResponse.success(citas));
    }

    @GetMapping("/rango")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CitaResponse>>> getCitasByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        log.info("GET /api/citas/rango?start={}&end={}", start, end);
        List<CitaResponse> citas = citaService.getCitasByDateRange(start, end);
        return ResponseEntity.ok(ApiResponse.success(citas));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<CitaResponse>> createCita(
            @Valid @RequestBody CitaRequest request) {
        log.info("POST /api/citas - creating cita");
        CitaResponse cita = citaService.createCita(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cita created successfully", cita));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<CitaResponse>> updateCita(
            @PathVariable Long id,
            @Valid @RequestBody CitaRequest request) {
        log.info("PUT /api/citas/{}", id);
        CitaResponse cita = citaService.updateCita(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cita updated successfully", cita));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCita(@PathVariable Long id) {
        log.info("DELETE /api/citas/{}", id);
        citaService.deleteCita(id);
        return ResponseEntity.ok(ApiResponse.success("Cita deleted successfully", null));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<CitaResponse>> updateCitaEstado(
            @PathVariable Long id,
            @RequestParam EstadoCita estado) {
        log.info("PATCH /api/citas/{}/estado?estado={}", id, estado);
        CitaResponse cita = citaService.updateCitaEstado(id, estado);
        return ResponseEntity.ok(ApiResponse.success("Cita estado updated successfully", cita));
    }
}
