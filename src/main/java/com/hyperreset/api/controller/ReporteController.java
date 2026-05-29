package com.hyperreset.api.controller;

import com.hyperreset.api.dto.request.ReporteRequest;
import com.hyperreset.api.dto.response.ApiResponse;
import com.hyperreset.api.dto.response.ReporteResponse;
import com.hyperreset.api.security.CurrentUser;
import com.hyperreset.api.service.ReporteService;
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
@RequestMapping("/api/reportes")
public class ReporteController {

    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReporteResponse>> getReporteById(@PathVariable Long id) {
        log.info("GET /api/reportes/{}", id);
        ReporteResponse reporte = reporteService.getReporteById(id);
        return ResponseEntity.ok(ApiResponse.success(reporte));
    }

    @GetMapping("/deportista/{deportistaId}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReporteResponse>>> getReportesByDeportista(
            @PathVariable Long deportistaId) {
        log.info("GET /api/reportes/deportista/{}", deportistaId);
        List<ReporteResponse> reportes = reporteService.getReportesByDeportista(deportistaId);
        return ResponseEntity.ok(ApiResponse.success(reportes));
    }

    @GetMapping("/test/{testId}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReporteResponse>>> getReportesByTest(
            @PathVariable Long testId) {
        log.info("GET /api/reportes/test/{}", testId);
        List<ReporteResponse> reportes = reporteService.getReportesByTest(testId);
        return ResponseEntity.ok(ApiResponse.success(reportes));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReporteResponse>> generarReporte(
            @Valid @RequestBody ReporteRequest request,
            @CurrentUser Long userId) {
        log.info("POST /api/reportes - generating reporte by userId: {}", userId);
        ReporteResponse reporte = reporteService.generarReporte(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reporte generated successfully", reporte));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReporte(@PathVariable Long id) {
        log.info("DELETE /api/reportes/{}", id);
        reporteService.deleteReporte(id);
        return ResponseEntity.ok(ApiResponse.success("Reporte deleted successfully", null));
    }
}
