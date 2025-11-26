package com.equipmentpredictor.service;

import com.equipmentpredictor.dto.EquipmentDTO;
import com.equipmentpredictor.exception.ResourceNotFoundException;
import com.equipmentpredictor.model.Equipment;
import com.equipmentpredictor.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    /**
     * Get all equipment
     */
    public List<EquipmentDTO> getAllEquipment() {
        return equipmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get equipment by ID
     */
    public EquipmentDTO getEquipmentById(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));
        return convertToDTO(equipment);
    }

    /**
     * Create new equipment
     */
    @Transactional
    public EquipmentDTO createEquipment(EquipmentDTO dto) {
        Equipment equipment = new Equipment();
        equipment.setName(dto.getName());
        equipment.setType(dto.getType());
        equipment.setLocation(dto.getLocation());
        equipment.setInstallDate(dto.getInstallDate());
        equipment.setNotes(dto.getNotes());

        Equipment saved = equipmentRepository.save(equipment);
        log.info("Created new equipment: id={}, name={}", saved.getId(), saved.getName());
        
        return convertToDTO(saved);
    }

    /**
     * Update existing equipment
     */
    @Transactional
    public EquipmentDTO updateEquipment(Long id, EquipmentDTO dto) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));

        equipment.setName(dto.getName());
        equipment.setType(dto.getType());
        equipment.setLocation(dto.getLocation());
        equipment.setInstallDate(dto.getInstallDate());
        equipment.setNotes(dto.getNotes());

        Equipment updated = equipmentRepository.save(equipment);
        log.info("Updated equipment: id={}, name={}", updated.getId(), updated.getName());
        
        return convertToDTO(updated);
    }

    /**
     * Delete equipment
     */
    @Transactional
    public void deleteEquipment(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipment not found with id: " + id);
        }
        
        equipmentRepository.deleteById(id);
        log.info("Deleted equipment: id={}", id);
    }

    /**
     * Search equipment by name
     */
    public List<EquipmentDTO> searchEquipmentByName(String name) {
        return equipmentRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to DTO
     */
    private EquipmentDTO convertToDTO(Equipment equipment) {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setId(equipment.getId());
        dto.setName(equipment.getName());
        dto.setType(equipment.getType());
        dto.setLocation(equipment.getLocation());
        dto.setInstallDate(equipment.getInstallDate());
        dto.setNotes(equipment.getNotes());
        return dto;
    }
}
