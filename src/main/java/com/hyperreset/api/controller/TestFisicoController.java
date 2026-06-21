package com.hyperreset.api.controller;

import com.hyperreset.api.dto.request.TestFisicoRequest;
import com.hyperreset.api.dto.response.ApiResponse;
import com.hyperreset.api.dto.response.TestFisicoResponse;
import com.hyperreset.api.security.CurrentUser;
import com.hyperreset.api.service.TestFisicoService;
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
@RequestMapping("/api/test-fisicos")
public class TestFisicoController {

    private static final Logger log = LoggerFactory.getLogger(TestFisicoController.class);

    @Autowired
    private TestFisicoService testFisicoService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<TestFisicoResponse>>> getAllTests() {
        log.debug("GET /api/test-fisicos");
        List<TestFisicoResponse> tests = testFisicoService.getAllTests();
        return ResponseEntity.ok(ApiResponse.success(tests));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TestFisicoResponse>> getTestById(@PathVariable Long id) {
        log.debug("GET /api/test-fisicos/{}", id);
        TestFisicoResponse test = testFisicoService.getTestById(id);
        return ResponseEntity.ok(ApiResponse.success(test));
    }

    @GetMapping("/deportista/{deportistaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<TestFisicoResponse>>> getTestsByDeportista(
            @PathVariable Long deportistaId) {
        log.debug("GET /api/test-fisicos/deportista/{}", deportistaId);
        List<TestFisicoResponse> tests = testFisicoService.getTestsByDeportista(deportistaId);
        return ResponseEntity.ok(ApiResponse.success(tests));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COACH', 'DEPORTISTA')")
    public ResponseEntity<ApiResponse<TestFisicoResponse>> createTest(
            @Valid @RequestBody TestFisicoRequest request,
            @CurrentUser Long userId) {
        log.debug("POST /api/test-fisicos - deportistaId: {}, userId: {}", request.getDeportistaId(), userId);
        TestFisicoResponse test = testFisicoService.createTest(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Test físico creado exitosamente", test));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<TestFisicoResponse>> updateTest(
            @PathVariable Long id,
            @Valid @RequestBody TestFisicoRequest request) {
        log.debug("PUT /api/test-fisicos/{}", id);
        TestFisicoResponse test = testFisicoService.updateTest(id, request);
        return ResponseEntity.ok(ApiResponse.success("Test físico actualizado exitosamente", test));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<Void>> deleteTest(@PathVariable Long id) {
        log.debug("DELETE /api/test-fisicos/{}", id);
        testFisicoService.deleteTest(id);
        return ResponseEntity.ok(ApiResponse.success("Test físico eliminado exitosamente", null));
    }

    @PatchMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('COACH', 'DEPORTISTA')")
    public ResponseEntity<ApiResponse<TestFisicoResponse>> completarTest(@PathVariable Long id) {
        log.debug("PATCH /api/test-fisicos/{}/completar", id);
        TestFisicoResponse test = testFisicoService.completarTest(id);
        return ResponseEntity.ok(ApiResponse.success("Test físico completado exitosamente", test));
    }
}
