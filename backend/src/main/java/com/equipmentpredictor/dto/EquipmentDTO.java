package com.equipmentpredictor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDTO {
    private Long id;
    
    @NotBlank(message = "Equipment name is required")
    private String name;
    
    @NotBlank(message = "Equipment type is required")
    private String type;
    
    private String location;
    private LocalDate installDate;
    private String notes;
}
