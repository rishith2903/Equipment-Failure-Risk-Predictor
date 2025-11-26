package com.equipmentpredictor.controller;

import com.equipmentpredictor.dto.AlertDTO;
import com.equipmentpredictor.dto.RiskResponseDTO;
import com.equipmentpredictor.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RiskController {

    private final AlertService alertService;

    @GetMapping("/equipment/{id}/risk/latest")
    public ResponseEntity<RiskResponseDTO> getLatestRisk(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getLatestRisk(id));
    }

    @GetMapping("/equipment/{id}/risk/history")
    public ResponseEntity<List<RiskResponseDTO>> getRiskHistory(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "100") Integer limit) {
        return ResponseEntity.ok(alertService.getRiskHistory(id, limit));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<AlertDTO>> getAlerts(
            @RequestParam(required = false) String level,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        return ResponseEntity.ok(alertService.getAlerts(level, limit));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(alertService.getDashboardStats());
    }
}
