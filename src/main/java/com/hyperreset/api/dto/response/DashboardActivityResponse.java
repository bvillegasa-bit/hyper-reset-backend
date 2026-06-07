package com.hyperreset.api.dto.response;

import java.util.List;

/**
 * Paginated response DTO for the activity endpoint GET /api/dashboard/actividad.
 * Contains a page of recent activity items along with pagination metadata.
 */
public class DashboardActivityResponse {

    private List<ActividadRecienteItem> items;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public DashboardActivityResponse() {
    }

    public DashboardActivityResponse(List<ActividadRecienteItem> items, int currentPage, int totalPages, long totalItems) {
        this.items = items;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }

    public List<ActividadRecienteItem> getItems() {
        return items;
    }

    public void setItems(List<ActividadRecienteItem> items) {
        this.items = items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }
}
