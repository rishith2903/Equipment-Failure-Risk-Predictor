package com.equipmentpredictor.service;

import com.equipmentpredictor.dto.RiskResponseDTO;
import com.equipmentpredictor.model.RiskEvent;
import com.equipmentpredictor.model.SensorLog;
import com.equipmentpredictor.repository.EquipmentRepository;
import com.equipmentpredictor.repository.RiskEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AI-based Risk Prediction Service
 * Implements weighted risk formula: riskScore = 0.4*sT + 0.35*sV + 0.25*sL
 * Where sT, sV, sL are normalized scores (0-100) for temperature, vibration,
 * and load
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskPredictionService {

    private final RiskEventRepository riskEventRepository;
    private final EquipmentRepository equipmentRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    // Normalization ranges
    private static final BigDecimal TEMP_MIN = BigDecimal.ZERO;
    private static final BigDecimal TEMP_MAX = new BigDecimal("150");
    private static final BigDecimal VIB_MIN = BigDecimal.ZERO;
    private static final BigDecimal VIB_MAX = new BigDecimal("50");
    private static final BigDecimal LOAD_MIN = BigDecimal.ZERO;
    private static final BigDecimal LOAD_MAX = new BigDecimal("100");

    // Weights for risk calculation (injected from application.properties)
    @org.springframework.beans.factory.annotation.Value("${risk.calculation.weight.temperature:0.40}")
    private BigDecimal WEIGHT_TEMP;

    @org.springframework.beans.factory.annotation.Value("${risk.calculation.weight.vibration:0.35}")
    private BigDecimal WEIGHT_VIB;

    @org.springframework.beans.factory.annotation.Value("${risk.calculation.weight.load:0.25}")
    private BigDecimal WEIGHT_LOAD;

    /**
     * Calculate risk score from sensor log data
     */
    @Transactional
    public RiskResponseDTO calculateRisk(SensorLog sensorLog) {
        // Normalize sensor readings to 0-100 scale
        BigDecimal normalizedTemp = normalize(sensorLog.getTemperature(), TEMP_MIN, TEMP_MAX);
        BigDecimal normalizedVib = normalize(sensorLog.getVibration(), VIB_MIN, VIB_MAX);
        BigDecimal normalizedLoad = normalize(sensorLog.getLoadPercentage(), LOAD_MIN, LOAD_MAX);

        // Calculate weighted risk score
        BigDecimal riskScore = normalizedTemp.multiply(WEIGHT_TEMP)
                .add(normalizedVib.multiply(WEIGHT_VIB))
                .add(normalizedLoad.multiply(WEIGHT_LOAD))
                .setScale(2, RoundingMode.HALF_UP);

        // Determine risk level
        RiskEvent.RiskLevel riskLevel = determineRiskLevel(riskScore);

        // Identify primary contributing factor
        String reason = identifyPrimaryReason(normalizedTemp, normalizedVib, normalizedLoad,
                sensorLog.getTemperature(),
                sensorLog.getVibration(),
                sensorLog.getLoadPercentage());

        log.info("Calculated risk for equipment {}: score={}, level={}, reason={}",
                sensorLog.getEquipmentId(), riskScore, riskLevel, reason);

        // Check if we need to create a risk event
        createRiskEventIfNeeded(sensorLog.getEquipmentId(), sensorLog.getTimestamp(),
                riskScore, riskLevel, reason);

        // Build response DTO
        String equipmentName = equipmentRepository.findById(sensorLog.getEquipmentId())
                .map(e -> e.getName())
                .orElse("Unknown");

        RiskResponseDTO riskResponseDTO = RiskResponseDTO.builder()
                .equipmentId(sensorLog.getEquipmentId())
                .equipmentName(equipmentName)
                .timestamp(sensorLog.getTimestamp())
                .riskScore(riskScore)
                .riskLevel(riskLevel)
                .reason(reason)
                .temperature(sensorLog.getTemperature())
                .vibration(sensorLog.getVibration())
                .loadPercentage(sensorLog.getLoadPercentage())
                .build();

        // Broadcast HIGH or CRITICAL risk events via WebSocket
        if (riskLevel == RiskEvent.RiskLevel.HIGH || riskLevel == RiskEvent.RiskLevel.CRITICAL) {
            try {
                messagingTemplate.convertAndSend("/topic/alerts", riskResponseDTO);
                log.info("Broadcasted {} risk alert for equipment {} via WebSocket",
                        riskLevel, equipmentName);
            } catch (Exception e) {
                log.error("Failed to broadcast WebSocket alert", e);
            }
        }

        return riskResponseDTO;
    }

    /**
     * Normalize a value to 0-100 scale
     */
    private BigDecimal normalize(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value.compareTo(min) <= 0)
            return BigDecimal.ZERO;
        if (value.compareTo(max) >= 0)
            return new BigDecimal("100");

        return value.subtract(min)
                .multiply(new BigDecimal("100"))
                .divide(max.subtract(min), 2, RoundingMode.HALF_UP);
    }

    /**
     * Determine risk level based on risk score
     */
    private RiskEvent.RiskLevel determineRiskLevel(BigDecimal riskScore) {
        if (riskScore.compareTo(new BigDecimal("85")) >= 0) {
            return RiskEvent.RiskLevel.CRITICAL;
        } else if (riskScore.compareTo(new BigDecimal("65")) >= 0) {
            return RiskEvent.RiskLevel.HIGH;
        } else if (riskScore.compareTo(new BigDecimal("40")) >= 0) {
            return RiskEvent.RiskLevel.MEDIUM;
        } else {
            return RiskEvent.RiskLevel.LOW;
        }
    }

    /**
     * Identify which metric contributed most to the risk score
     */
    private String identifyPrimaryReason(BigDecimal normTemp, BigDecimal normVib, BigDecimal normLoad,
            BigDecimal actualTemp, BigDecimal actualVib, BigDecimal actualLoad) {
        Map<String, BigDecimal> contributions = new HashMap<>();
        contributions.put("Temperature", normTemp.multiply(WEIGHT_TEMP));
        contributions.put("Vibration", normVib.multiply(WEIGHT_VIB));
        contributions.put("Load", normLoad.multiply(WEIGHT_LOAD));

        String primaryFactor = contributions.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        return String.format("Primary risk factor: %s (%.1f%s)",
                primaryFactor,
                getPrimaryValue(primaryFactor, actualTemp, actualVib, actualLoad),
                getUnit(primaryFactor));
    }

    private BigDecimal getPrimaryValue(String factor, BigDecimal temp, BigDecimal vib, BigDecimal load) {
        return switch (factor) {
            case "Temperature" -> temp;
            case "Vibration" -> vib;
            case "Load" -> load;
            default -> BigDecimal.ZERO;
        };
    }

    private String getUnit(String factor) {
        return switch (factor) {
            case "Temperature" -> "°C";
            case "Vibration" -> " mm/s";
            case "Load" -> "%";
            default -> "";
        };
    }

    /**
     * Create risk event if:
     * 1. Risk level is MEDIUM or higher, OR
     * 2. Risk level changed from previous event
     */
    private void createRiskEventIfNeeded(Long equipmentId, java.time.LocalDateTime timestamp,
            BigDecimal riskScore, RiskEvent.RiskLevel riskLevel, String reason) {
        Optional<RiskEvent> lastEvent = riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(equipmentId);

        boolean shouldCreate = false;

        if (riskLevel != RiskEvent.RiskLevel.LOW) {
            // Always create for MEDIUM, HIGH, CRITICAL
            shouldCreate = true;
        } else if (lastEvent.isPresent() && lastEvent.get().getRiskLevel() != RiskEvent.RiskLevel.LOW) {
            // Risk level changed to LOW from higher level
            shouldCreate = true;
        }

        if (shouldCreate) {
            RiskEvent event = new RiskEvent();
            event.setEquipmentId(equipmentId);
            event.setTimestamp(timestamp);
            event.setRiskScore(riskScore);
            event.setRiskLevel(riskLevel);
            event.setReason(reason);
            riskEventRepository.save(event);

            log.info("Created risk event for equipment {}: level={}", equipmentId, riskLevel);
        }
    }
}
