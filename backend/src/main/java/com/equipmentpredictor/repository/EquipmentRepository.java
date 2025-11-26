package com.equipmentpredictor.repository;

import com.equipmentpredictor.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    
    List<Equipment> findByType(String type);
    
    List<Equipment> findByNameContainingIgnoreCase(String name);
}
