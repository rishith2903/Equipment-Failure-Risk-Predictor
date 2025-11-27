package com.equipmentpredictor.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorLogDTO {
    private Long id;

    private Long equipmentId;

    private LocalDateTime timestamp;

    @NotNull(message = "Temperature is required")
    @DecimalMin(value = "-50.0", message = "Temperature must be at least -50°C")
    @DecimalMax(value = "200.0", message = "Temperature must not exceed 200°C")
    private BigDecimal temperature;

    @NotNull(message = "Vibration is required")
    @DecimalMin(value = "0.0", message = "Vibration must be at least 0 mm/s")
    @DecimalMax(value = "100.0", message = "Vibration must not exceed 100 mm/s")
    private BigDecimal vibration;

    @NotNull(message = "Load percentage is required")
    @DecimalMin(value = "0.0", message = "Load percentage must be at least 0%")
    @DecimalMax(value = "100.0", message = "Load percentage must not exceed 100%")
    private BigDecimal loadPercentage;
}
