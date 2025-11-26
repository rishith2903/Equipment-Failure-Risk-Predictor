package com.equipmentpredictor.service;

import com.equipmentpredictor.dto.SensorLogDTO;
import com.equipmentpredictor.exception.ResourceNotFoundException;
import com.equipmentpredictor.model.SensorLog;
import com.equipmentpredictor.repository.EquipmentRepository;
import com.equipmentpredictor.repository.SensorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorLogService {

    private final SensorLogRepository sensorLogRepository;
    private final EquipmentRepository equipmentRepository;
    private final RiskPredictionService riskPredictionService;

    /**
     * Add sensor log for equipment
     */
    @Transactional
    public SensorLogDTO addSensorLog(SensorLogDTO dto) {
        // Validate equipment exists
        if (!equipmentRepository.existsById(dto.getEquipmentId())) {
            throw new ResourceNotFoundException("Equipment not found with id: " + dto.getEquipmentId());
        }

        SensorLog sensorLog = new SensorLog();
        sensorLog.setEquipmentId(dto.getEquipmentId());
        sensorLog.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());
        sensorLog.setTemperature(dto.getTemperature());
        sensorLog.setVibration(dto.getVibration());
        sensorLog.setLoadPercentage(dto.getLoadPercentage());

        SensorLog saved = sensorLogRepository.save(sensorLog);
        log.info("Added sensor log for equipment {}: temp={}, vib={}, load={}", 
                 saved.getEquipmentId(), saved.getTemperature(), saved.getVibration(), saved.getLoadPercentage());

        // Calculate risk for this sensor log
        riskPredictionService.calculateRisk(saved);

        return convertToDTO(saved);
    }

    /**
     * Get sensor logs for equipment with pagination
     */
    public List<SensorLogDTO> getSensorLogs(Long equipmentId, Integer limit, String order) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new ResourceNotFoundException("Equipment not found with id: " + equipmentId);
        }

        Pageable pageable = PageRequest.of(0, limit != null ? limit : 100);
        
        List<SensorLog> logs;
        if ("asc".equalsIgnoreCase(order)) {
            logs = sensorLogRepository.findByEquipmentIdOrderByTimestampAsc(equipmentId, pageable);
        } else {
            logs = sensorLogRepository.findByEquipmentIdOrderByTimestampDesc(equipmentId, pageable);
        }

        return logs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get sensor logs within date range
     */
    public List<SensorLogDTO> getSensorLogsByDateRange(Long equipmentId, LocalDateTime from, 
                                                       LocalDateTime to, Integer limit) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new ResourceNotFoundException("Equipment not found with id: " + equipmentId);
        }

        Pageable pageable = PageRequest.of(0, limit != null ? limit : 100);
        
        List<SensorLog> logs = sensorLogRepository.findByEquipmentIdAndTimestampBetween(
            equipmentId, from, to, pageable);

        return logs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get latest sensor log for equipment
     */
    public SensorLogDTO getLatestSensorLog(Long equipmentId) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new ResourceNotFoundException("Equipment not found with id: " + equipmentId);
        }

        return sensorLogRepository.findFirstByEquipmentIdOrderByTimestampDesc(equipmentId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No sensor logs found for equipment: " + equipmentId));
    }

    /**
     * Convert entity to DTO
     */
    private SensorLogDTO convertToDTO(SensorLog log) {
        SensorLogDTO dto = new SensorLogDTO();
        dto.setId(log.getId());
        dto.setEquipmentId(log.getEquipmentId());
        dto.setTimestamp(log.getTimestamp());
        dto.setTemperature(log.getTemperature());
        dto.setVibration(log.getVibration());
        dto.setLoadPercentage(log.getLoadPercentage());
        return dto;
    }
}
