package com.hyperreset.api.controller;

import com.hyperreset.api.dto.response.ApiResponse;
import com.hyperreset.api.dto.response.DashboardCoachResponse;
import com.hyperreset.api.dto.response.DashboardDeportistaResponse;
import com.hyperreset.api.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for dashboard endpoints.
 * Provides aggregated data for deportista and coach dashboards.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    /**
     * GET /api/dashboard/deportista/{id}
     * Returns dashboard data for a deportista: next appointment, weekly progress,
     * monthly goal, and achievements.
     */
    @GetMapping("/deportista/{id}")
    @PreAuthorize("hasAnyRole('DEPORTISTA', 'COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<DashboardDeportistaResponse>> getDashboardDeportista(
            @PathVariable Long id) {
        log.info("GET /api/dashboard/deportista/{}", id);
        DashboardDeportistaResponse dashboard = dashboardService.getDashboardDeportista(id);
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    /**
     * GET /api/dashboard/coach/{id}
     * Returns dashboard data for a coach: statistics, recent activity,
     * and upcoming appointments.
     */
    @GetMapping("/coach/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public ResponseEntity<ApiResponse<DashboardCoachResponse>> getDashboardCoach(
            @PathVariable Long id) {
        log.info("GET /api/dashboard/coach/{}", id);
        DashboardCoachResponse dashboard = dashboardService.getDashboardCoach(id);
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}
