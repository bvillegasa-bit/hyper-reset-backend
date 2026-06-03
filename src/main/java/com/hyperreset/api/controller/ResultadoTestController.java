package com.hyperreset.api.controller;

import com.hyperreset.api.dto.request.ResultadoRequest;
import com.hyperreset.api.dto.response.ApiResponse;
import com.hyperreset.api.dto.response.ResultadoResponse;
import com.hyperreset.api.dto.response.TipoTestEstadoResponse;
import com.hyperreset.api.service.ResultadoTestService;
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
@RequestMapping("/api/resultados")
public class ResultadoTestController {

    private static final Logger log = LoggerFactory.getLogger(ResultadoTestController.class);

    @Autowired
    private ResultadoTestService resultadoTestService;

    @GetMapping("/test/{testId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ResultadoResponse>>> getResultadosByTest(
            @PathVariable Long testId) {
        log.debug("GET /api/resultados/test/{}", testId);
        List<ResultadoResponse> resultados = resultadoTestService.getResultadosByTest(testId);
        return ResponseEntity.ok(ApiResponse.success(resultados));
    }

    @GetMapping("/deportista/{deportistaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ResultadoResponse>>> getResultadosByDeportista(
            @PathVariable Long deportistaId) {
        log.debug("GET /api/resultados/deportista/{}", deportistaId);
        List<ResultadoResponse> resultados = resultadoTestService.getResultadosByDeportista(deportistaId);
        return ResponseEntity.ok(ApiResponse.success(resultados));
    }

    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<ResultadoResponse>> createResultado(
            @Valid @RequestBody ResultadoRequest request) {
        log.debug("POST /api/resultados - testId: {}", request.getTestId());
        ResultadoResponse resultado = resultadoTestService.createResultado(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resultado creado exitosamente", resultado));
    }

    @GetMapping("/tipos-con-estado/{deportistaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<TipoTestEstadoResponse>>> getTiposTestConEstado(
            @PathVariable Long deportistaId) {
        log.info("GET /api/resultados/tipos-con-estado/{}", deportistaId);
        List<TipoTestEstadoResponse> tipos = resultadoTestService.getTiposTestConEstado(deportistaId);
        return ResponseEntity.ok(ApiResponse.success(tipos));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<Void>> deleteResultado(@PathVariable Long id) {
        log.debug("DELETE /api/resultados/{}", id);
        resultadoTestService.deleteResultado(id);
        return ResponseEntity.ok(ApiResponse.success("Resultado eliminado exitosamente", null));
    }
}
