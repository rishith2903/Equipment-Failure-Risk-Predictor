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
                        Pageable pageable);

        @Query(value = "SELECT COUNT(DISTINCT equipment_id) FROM risk_event r1 " +
                        "WHERE r1.risk_level = :#{#level.name()} " +
                        "AND r1.timestamp = (SELECT MAX(r2.timestamp) FROM risk_event r2 WHERE r2.equipment_id = r1.equipment_id)", nativeQuery = true)
        long countEquipmentByLatestRiskLevel(@Param("level") RiskEvent.RiskLevel level);
}
