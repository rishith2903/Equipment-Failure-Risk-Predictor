package com.equipmentpredictor.dto;

import com.equipmentpredictor.model.RiskEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskResponseDTO {
    private Long equipmentId;
    private String equipmentName;
    private LocalDateTime timestamp;
    private BigDecimal riskScore;
    private RiskEvent.RiskLevel riskLevel;
    private String reason;
    private BigDecimal temperature;
    private BigDecimal vibration;
    private BigDecimal loadPercentage;
}
