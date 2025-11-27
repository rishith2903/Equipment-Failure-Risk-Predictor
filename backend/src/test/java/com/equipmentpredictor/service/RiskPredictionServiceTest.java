package com.equipmentpredictor.service;

import com.equipmentpredictor.dto.RiskResponseDTO;
import com.equipmentpredictor.model.Equipment;
import com.equipmentpredictor.model.RiskEvent;
import com.equipmentpredictor.model.SensorLog;
import com.equipmentpredictor.repository.EquipmentRepository;
import com.equipmentpredictor.repository.RiskEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RiskPredictionService
 * Tests risk calculation logic, event creation, and edge cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RiskPredictionService Unit Tests")
class RiskPredictionServiceTest {

    @Mock
    private RiskEventRepository riskEventRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private RiskPredictionService riskPredictionService;

    private Equipment testEquipment;

    @BeforeEach
    void setUp() {
        // Initialize service with default weight values
        ReflectionTestUtils.setField(riskPredictionService, "WEIGHT_TEMP", new BigDecimal("0.40"));
        ReflectionTestUtils.setField(riskPredictionService, "WEIGHT_VIB", new BigDecimal("0.35"));
        ReflectionTestUtils.setField(riskPredictionService, "WEIGHT_LOAD", new BigDecimal("0.25"));

        // Setup test equipment
        testEquipment = new Equipment();
        testEquipment.setId(1L);
        testEquipment.setName("Test Turbine");
        testEquipment.setType("TURBINE");
    }

    @Test
    @DisplayName("Test Case 1: Calculate CRITICAL risk with high sensor readings")
    void testCalculateCriticalRisk() {
        // Given: High sensor readings
        SensorLog sensorLog = createSensorLog(1L, new BigDecimal("140"), new BigDecimal("45"), new BigDecimal("95"));

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(testEquipment));
        when(riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(1L)).thenReturn(Optional.empty());

        // When: Calculate risk
        RiskResponseDTO result = riskPredictionService.calculateRisk(sensorLog);

        // Then: Should be CRITICAL risk
        assertNotNull(result);
        assertEquals(RiskEvent.RiskLevel.CRITICAL, result.getRiskLevel());
        assertTrue(result.getRiskScore().compareTo(new BigDecimal("85")) >= 0,
                "Risk score should be >= 85 for CRITICAL level");
        assertEquals("Test Turbine", result.getEquipmentName());

        // Verify risk event was created
        verify(riskEventRepository, times(1)).save(any(RiskEvent.class));
    }

    @Test
    @DisplayName("Test Case 2: Calculate LOW risk with low sensor readings")
    void testCalculateLowRisk() {
        // Given: Low sensor readings
        SensorLog sensorLog = createSensorLog(1L, new BigDecimal("20"), new BigDecimal("5"), new BigDecimal("10"));

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(testEquipment));
        when(riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(1L)).thenReturn(Optional.empty());

        // When: Calculate risk
        RiskResponseDTO result = riskPredictionService.calculateRisk(sensorLog);

        // Then: Should be LOW risk
        assertNotNull(result);
        assertEquals(RiskEvent.RiskLevel.LOW, result.getRiskLevel());
        assertTrue(result.getRiskScore().compareTo(new BigDecimal("40")) < 0,
                "Risk score should be < 40 for LOW level");
        assertEquals("Test Turbine", result.getEquipmentName());

        // Verify NO risk event was created for LOW risk
        verify(riskEventRepository, never()).save(any(RiskEvent.class));
    }

    @Test
    @DisplayName("Test Case 3: Verify createRiskEventIfNeeded is called for HIGH risk")
    void testCreateRiskEventForHighRisk() {
        // Given: HIGH risk sensor readings (risk score between 65-85)
        SensorLog sensorLog = createSensorLog(1L, new BigDecimal("100"), new BigDecimal("30"), new BigDecimal("70"));

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(testEquipment));
        when(riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(1L)).thenReturn(Optional.empty());

        // When: Calculate risk
        RiskResponseDTO result = riskPredictionService.calculateRisk(sensorLog);

        // Then: Should be HIGH or CRITICAL risk, and event should be created
        assertNotNull(result);
        assertTrue(result.getRiskLevel() == RiskEvent.RiskLevel.HIGH ||
                result.getRiskLevel() == RiskEvent.RiskLevel.CRITICAL,
                "Risk level should be HIGH or CRITICAL");

        // Verify risk event was created with correct data
        ArgumentCaptor<RiskEvent> eventCaptor = ArgumentCaptor.forClass(RiskEvent.class);
        verify(riskEventRepository, times(1)).save(eventCaptor.capture());

        RiskEvent savedEvent = eventCaptor.getValue();
        assertEquals(1L, savedEvent.getEquipmentId());
        assertNotNull(savedEvent.getRiskScore());
        assertNotNull(savedEvent.getReason());
    }

    @Test
    @DisplayName("Test Case 4: Verify NO risk event created for LOW risk with no previous events")
    void testNoRiskEventForLowRisk() {
        // Given: LOW risk sensor readings
        SensorLog sensorLog = createSensorLog(1L, new BigDecimal("15"), new BigDecimal("3"), new BigDecimal("5"));

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(testEquipment));
        when(riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(1L)).thenReturn(Optional.empty());

        // When: Calculate risk
        RiskResponseDTO result = riskPredictionService.calculateRisk(sensorLog);

        // Then: Should be LOW risk
        assertEquals(RiskEvent.RiskLevel.LOW, result.getRiskLevel());

        // Verify NO risk event was created
        verify(riskEventRepository, never()).save(any(RiskEvent.class));
    }

    @Test
    @DisplayName("Test Case 5: Verify risk event created when transitioning from HIGH to LOW")
    void testRiskEventCreatedOnTransitionToLow() {
        // Given: Previous HIGH risk event exists, now LOW risk readings
        RiskEvent previousEvent = new RiskEvent();
        previousEvent.setEquipmentId(1L);
        previousEvent.setRiskLevel(RiskEvent.RiskLevel.HIGH);
        previousEvent.setRiskScore(new BigDecimal("70"));

        SensorLog sensorLog = createSensorLog(1L, new BigDecimal("20"), new BigDecimal("5"), new BigDecimal("10"));

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(testEquipment));
        when(riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(1L))
                .thenReturn(Optional.of(previousEvent));

        // When: Calculate risk
        RiskResponseDTO result = riskPredictionService.calculateRisk(sensorLog);

        // Then: Should be LOW risk
        assertEquals(RiskEvent.RiskLevel.LOW, result.getRiskLevel());

        // Verify risk event WAS created (transition from HIGH to LOW)
        verify(riskEventRepository, times(1)).save(any(RiskEvent.class));
    }

    @Test
    @DisplayName("Test Case 6: Validate risk score calculation formula")
    void testRiskScoreCalculationFormula() {
        // Given: Known sensor values
        // temp=100 -> normalized to 66.67, vib=25 -> normalized to 50, load=50 ->
        // normalized to 50
        // Expected risk score = (66.67 * 0.4) + (50 * 0.35) + (50 * 0.25) = 26.67 +
        // 17.5 + 12.5 = 56.67
        SensorLog sensorLog = createSensorLog(1L, new BigDecimal("100"), new BigDecimal("25"), new BigDecimal("50"));

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(testEquipment));
        when(riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(1L)).thenReturn(Optional.empty());

        // When: Calculate risk
        RiskResponseDTO result = riskPredictionService.calculateRisk(sensorLog);

        // Then: Risk score should be approximately 56.67 (MEDIUM level)
        assertNotNull(result);
        assertEquals(RiskEvent.RiskLevel.MEDIUM, result.getRiskLevel());
        assertTrue(result.getRiskScore().compareTo(new BigDecimal("55")) >= 0 &&
                result.getRiskScore().compareTo(new BigDecimal("58")) <= 0,
                "Risk score should be approximately 56.67");
    }

    @Test
    @DisplayName("Test Case 7: Verify equipment name fallback when equipment not found")
    void testEquipmentNameFallback() {
        // Given: Equipment not found in database
        SensorLog sensorLog = createSensorLog(999L, new BigDecimal("50"), new BigDecimal("20"), new BigDecimal("30"));

        when(equipmentRepository.findById(999L)).thenReturn(Optional.empty());
        when(riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(999L)).thenReturn(Optional.empty());

        // When: Calculate risk
        RiskResponseDTO result = riskPredictionService.calculateRisk(sensorLog);

        // Then: Should use "Unknown" as equipment name
        assertEquals("Unknown", result.getEquipmentName());
    }

    @Test
    @DisplayName("Test Case 8: Verify MEDIUM risk level calculation")
    void testMediumRiskLevel() {
        // Given: MEDIUM risk sensor readings (score between 40-65)
        SensorLog sensorLog = createSensorLog(1L, new BigDecimal("75"), new BigDecimal("20"), new BigDecimal("40"));

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(testEquipment));
        when(riskEventRepository.findFirstByEquipmentIdOrderByTimestampDesc(1L)).thenReturn(Optional.empty());

        // When: Calculate risk
        RiskResponseDTO result = riskPredictionService.calculateRisk(sensorLog);

        // Then: Should be MEDIUM risk
        assertNotNull(result);
        assertEquals(RiskEvent.RiskLevel.MEDIUM, result.getRiskLevel());
        assertTrue(result.getRiskScore().compareTo(new BigDecimal("40")) >= 0 &&
                result.getRiskScore().compareTo(new BigDecimal("65")) < 0,
                "Risk score should be between 40 and 65 for MEDIUM level");

        // Verify risk event was created for MEDIUM risk
        verify(riskEventRepository, times(1)).save(any(RiskEvent.class));
    }

    // Helper method to create SensorLog
    private SensorLog createSensorLog(Long equipmentId, BigDecimal temp, BigDecimal vib, BigDecimal load) {
        SensorLog log = new SensorLog();
        log.setEquipmentId(equipmentId);
        log.setTemperature(temp);
        log.setVibration(vib);
        log.setLoadPercentage(load);
        log.setTimestamp(LocalDateTime.now());
        return log;
    }
}
