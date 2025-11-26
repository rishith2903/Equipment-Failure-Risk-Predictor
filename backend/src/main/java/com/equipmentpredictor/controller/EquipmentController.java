package com.equipmentpredictor.controller;

import com.equipmentpredictor.dto.EquipmentDTO;
import com.equipmentpredictor.dto.SensorLogDTO;
import com.equipmentpredictor.service.EquipmentService;
import com.equipmentpredictor.service.SensorLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final SensorLogService sensorLogService;

    @GetMapping
    public ResponseEntity<List<EquipmentDTO>> getAllEquipment() {
        return ResponseEntity.ok(equipmentService.getAllEquipment());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDTO> getEquipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @PostMapping
    public ResponseEntity<EquipmentDTO> createEquipment(@Valid @RequestBody EquipmentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(equipmentService.createEquipment(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentDTO> updateEquipment(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentDTO dto) {
        return ResponseEntity.ok(equipmentService.updateEquipment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<EquipmentDTO>> searchEquipment(@RequestParam String name) {
        return ResponseEntity.ok(equipmentService.searchEquipmentByName(name));
    }

    // Sensor log endpoints for specific equipment
    @PostMapping("/{id}/logs")
    public ResponseEntity<SensorLogDTO> addSensorLog(
            @PathVariable Long id,
            @Valid @RequestBody SensorLogDTO dto) {
        dto.setEquipmentId(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sensorLogService.addSensorLog(dto));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<SensorLogDTO>> getSensorLogs(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false, defaultValue = "100") Integer limit,
            @RequestParam(required = false, defaultValue = "desc") String order) {
        
        if (from != null && to != null) {
            return ResponseEntity.ok(sensorLogService.getSensorLogsByDateRange(id, from, to, limit));
        } else {
            return ResponseEntity.ok(sensorLogService.getSensorLogs(id, limit, order));
        }
    }

    @GetMapping("/{id}/logs/latest")
    public ResponseEntity<SensorLogDTO> getLatestSensorLog(@PathVariable Long id) {
        return ResponseEntity.ok(sensorLogService.getLatestSensorLog(id));
    }
}
