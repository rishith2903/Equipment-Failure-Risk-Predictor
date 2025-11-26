package com.equipmentpredictor.repository;

import com.equipmentpredictor.model.SensorLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorLogRepository extends JpaRepository<SensorLog, Long> {
    
    List<SensorLog> findByEquipmentIdOrderByTimestampDesc(Long equipmentId, Pageable pageable);
    
    List<SensorLog> findByEquipmentIdOrderByTimestampAsc(Long equipmentId, Pageable pageable);
    
    @Query("SELECT s FROM SensorLog s WHERE s.equipmentId = :equipmentId " +
           "AND s.timestamp BETWEEN :from AND :to ORDER BY s.timestamp DESC")
    List<SensorLog> findByEquipmentIdAndTimestampBetween(
        @Param("equipmentId") Long equipmentId,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        Pageable pageable
    );
    
    Optional<SensorLog> findFirstByEquipmentIdOrderByTimestampDesc(Long equipmentId);
    
    @Query("SELECT COUNT(s) FROM SensorLog s WHERE s.equipmentId = :equipmentId")
    long countByEquipmentId(@Param("equipmentId") Long equipmentId);
}
