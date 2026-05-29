package com.hyperreset.api.controller;

import com.hyperreset.api.dto.request.MaterialRequest;
import com.hyperreset.api.dto.response.ApiResponse;
import com.hyperreset.api.dto.response.MaterialResponse;
import com.hyperreset.api.security.CurrentUser;
import com.hyperreset.api.service.MaterialService;
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
@RequestMapping("/api/materiales")
public class MaterialController {

    private static final Logger log = LoggerFactory.getLogger(MaterialController.class);

    @Autowired
    private MaterialService materialService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<MaterialResponse>>> getAllMateriales() {
        log.info("GET /api/materiales - list all");
        List<MaterialResponse> materiales = materialService.getAllMateriales();
        return ResponseEntity.ok(ApiResponse.success(materiales));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MaterialResponse>> getMaterialById(@PathVariable Long id) {
        log.info("GET /api/materiales/{}", id);
        MaterialResponse material = materialService.getMaterialById(id);
        return ResponseEntity.ok(ApiResponse.success(material));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<MaterialResponse>> createMaterial(
            @Valid @RequestBody MaterialRequest request,
            @CurrentUser Long userId) {
        log.info("POST /api/materiales - creating material by userId: {}", userId);
        MaterialResponse material = materialService.createMaterial(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Material created successfully", material));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<MaterialResponse>> updateMaterial(
            @PathVariable Long id,
            @Valid @RequestBody MaterialRequest request) {
        log.info("PUT /api/materiales/{}", id);
        MaterialResponse material = materialService.updateMaterial(id, request);
        return ResponseEntity.ok(ApiResponse.success("Material updated successfully", material));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(@PathVariable Long id) {
        log.info("DELETE /api/materiales/{}", id);
        materialService.deleteMaterial(id);
        return ResponseEntity.ok(ApiResponse.success("Material deleted successfully", null));
    }
}
