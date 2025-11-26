package com.equipmentpredictor.repository;

import com.equipmentpredictor.model.RiskEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskEventRepository extends JpaRepository<RiskEvent, Long> {
    
    Optional<RiskEvent> findFirstByEquipmentIdOrderByTimestampDesc(Long equipmentId);
    
    List<RiskEvent> findByEquipmentIdOrderByTimestampDesc(Long equipmentId, Pageable pageable);
    
    List<RiskEvent> findByRiskLevelOrderByTimestampDesc(RiskEvent.RiskLevel riskLevel, Pageable pageable);
    
    @Query("SELECT r FROM RiskEvent r WHERE r.riskLevel IN :levels ORDER BY r.timestamp DESC")
    List<RiskEvent> findByRiskLevelInOrderByTimestampDesc(
        @Param("levels") List<RiskEvent.RiskLevel> levels,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(DISTINCT r.equipmentId) FROM RiskEvent r " +
           "WHERE r.id IN (SELECT MAX(re.id) FROM RiskEvent re GROUP BY re.equipmentId) " +
           "AND r.riskLevel = :level")
    long countEquipmentByLatestRiskLevel(@Param("level") RiskEvent.RiskLevel level);
}
