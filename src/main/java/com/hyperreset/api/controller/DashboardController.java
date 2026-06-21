package com.hyperreset.api.controller;

import com.hyperreset.api.dto.response.ApiResponse;
import com.hyperreset.api.dto.response.DashboardActivityResponse;
import com.hyperreset.api.dto.response.DashboardCoachResponse;
import com.hyperreset.api.dto.response.DashboardDeportistaResponse;
import com.hyperreset.api.dto.response.ActividadRecienteItem;
import com.hyperreset.api.entity.Coach;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.CoachRepository;

import java.util.Collections;
import java.util.Optional;
import com.hyperreset.api.security.CurrentUser;
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

    @Autowired
    private CoachRepository coachRepository;

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

    /**
     * GET /api/dashboard/actividad
     * Returns a paginated list of recent activity for the authenticated coach.
     * Used by the "Ver toda la actividad" feature.
     *
     * @param page zero-based page index (default 0)
     * @param size page size (default 20)
     */
    @GetMapping("/actividad")
    @PreAuthorize("hasAnyRole('COACH', 'DEPORTISTA', 'ADMIN')")
    public ResponseEntity<ApiResponse<DashboardActivityResponse>> getActividad(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser Long userId) {

        log.info("GET /api/dashboard/actividad?page={}&size={} (userId={})", page, size, userId);

        Optional<Coach> coachOpt = coachRepository.findByUsuarioId(userId);
        if (coachOpt.isEmpty()) {
            DashboardActivityResponse emptyResponse = new DashboardActivityResponse();
            emptyResponse.setItems(Collections.emptyList());
            emptyResponse.setCurrentPage(0);
            emptyResponse.setTotalPages(0);
            emptyResponse.setTotalItems(0);
            return ResponseEntity.ok(ApiResponse.success(emptyResponse));
        }

        Coach coach = coachOpt.get();
        DashboardActivityResponse response = dashboardService.getActividad(coach.getIdCoach(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
