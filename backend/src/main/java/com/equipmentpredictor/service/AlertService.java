package com.equipmentpredictor.service;

import com.equipmentpredictor.dto.AlertDTO;
import com.equipmentpredictor.dto.RiskResponseDTO;
import com.equipmentpredictor.exception.ResourceNotFoundException;
import com.equipmentpredictor.model.Equipment;
import com.equipmentpredictor.model.RiskEvent;
import com.equipmentpredictor.repository.EquipmentRepository;
import com.equipmentpredictor.repository.RiskEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final RiskEventRepository riskEventRepository;
    private final EquipmentRepository equipmentRepository;

    /**
     * Get alerts filtered by risk level
     */
    public List<AlertDTO> getAlerts(String level, Integer limit) {
        Pageable pageable = PageRequest.of(0, limit != null ? limit : 50);

        List<RiskEvent> events;
        if (level == null || level.isEmpty()) {
            // Get MEDIUM, HIGH, and CRITICAL alerts
            events = riskEventRepository.findByRiskLevelInOrderByTimestampDesc(
                    List.of(RiskEvent.RiskLevel.MEDIUM, RiskEvent.RiskLevel.HIGH, RiskEvent.RiskLevel.CRITICAL),
                    pageable);
        } else {
            RiskEvent.RiskLevel riskLevel = RiskEvent.RiskLevel.valueOf(level.toUpperCase());
            events = riskEventRepository.findByRiskLevelOrderByTimestampDesc(riskLevel, pageable);
        }

        // Get equipment info for all events
        Map<Long, Equipment> equipmentMap = equipmentRepository.findAllById(
                events.stream().map(RiskEvent::getEquipmentId).distinct().collect(Collectors.toList())).stream()
                .collect(Collectors.toMap(Equipment::getId, e -> e));

        return events.stream()
                .map(event -> {
                    Equipment equipment = equipmentMap.get(event.getEquipmentId());
                    return AlertDTO.builder()
                            .id(event.getId())
                            .equipmentId(event.getEquipmentId())
                            .equipmentName(equipment != null ? equipment.getName() : "Unknown")
                            .equipmentType(equipment != null ? equipment.getType() : "Unknown")
                            .timestamp(event.getTimestamp())
                            .riskScore(event.getRiskScore())
                            .riskLevel(event.getRiskLevel())
                            .reason(event.getReason())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Get latest risk for equipment
     */
    public RiskResponseDTO getLatestRisk(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + equipmentId));

        RiskEvent latestRisk = riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No risk data found for equipment: " + equipmentId));

        return RiskResponseDTO.builder()
                .equipmentId(equipmentId)
                .equipmentName(equipment.getName())
                .timestamp(latestRisk.getTimestamp())
                .riskScore(latestRisk.getRiskScore())
                .riskLevel(latestRisk.getRiskLevel())
                .reason(latestRisk.getReason())
                .build();
    }

    /**
     * Get risk history for equipment
     */
    public List<RiskResponseDTO> getRiskHistory(Long equipmentId, Integer limit) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + equipmentId));

        Pageable pageable = PageRequest.of(0, limit != null ? limit : 100);
        List<RiskEvent> history = riskEventRepository.findByEquipmentIdOrderByTimestampDesc(equipmentId, pageable);

        return history.stream()
                .map(event -> RiskResponseDTO.builder()
                        .equipmentId(equipmentId)
                        .equipmentName(equipment.getName())
                        .timestamp(event.getTimestamp())
                        .riskScore(event.getRiskScore())
                        .riskLevel(event.getRiskLevel())
                        .reason(event.getReason())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Get dashboard statistics
     */
    public Map<String, Object> getDashboardStats() {
        List<Equipment> allEquipment = equipmentRepository.findAll();
        long totalEquipment = allEquipment.size();

        // Count equipment by their latest risk level
        long criticalCount = 0;
        long highCount = 0;
        long mediumCount = 0;
        long lowCount = 0;

        for (Equipment equipment : allEquipment) {
            // Get latest risk event for this equipment
            riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(equipment.getId())
                    .ifPresent(latestRisk -> {
                        // This won't work directly, need to handle outside lambda
                    });
        }

        // Better approach: collect all latest risks first
        Map<RiskEvent.RiskLevel, Long> riskCounts = allEquipment.stream()
                .map(equipment -> riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(equipment.getId()))
                .filter(optional -> optional.isPresent())
                .map(optional -> optional.get().getRiskLevel())
                .collect(Collectors.groupingBy(level -> level, Collectors.counting()));

        criticalCount = riskCounts.getOrDefault(RiskEvent.RiskLevel.CRITICAL, 0L);
        highCount = riskCounts.getOrDefault(RiskEvent.RiskLevel.HIGH, 0L);
        mediumCount = riskCounts.getOrDefault(RiskEvent.RiskLevel.MEDIUM, 0L);
        lowCount = riskCounts.getOrDefault(RiskEvent.RiskLevel.LOW, 0L);

        return Map.of(
                "totalEquipment", totalEquipment,
                "criticalEquipment", criticalCount,
                "highRiskEquipment", highCount,
                "mediumRiskEquipment", mediumCount,
                "lowRiskEquipment", lowCount);
    }
}
