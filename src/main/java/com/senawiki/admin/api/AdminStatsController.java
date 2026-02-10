package com.senawiki.admin.api;

import com.senawiki.admin.api.dto.AdminStatsResponse;
import com.senawiki.admin.service.AdminStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    private final AdminStatsService service;

    public AdminStatsController(AdminStatsService service) {
        this.service = service;
    }

    @GetMapping
    public AdminStatsResponse getStats() {
        return service.getStats();
    }
}
