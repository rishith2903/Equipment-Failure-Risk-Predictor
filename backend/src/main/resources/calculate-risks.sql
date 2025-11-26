-- SQL script to calculate and insert risk events from existing sensor logs
-- This implements the same AI risk calculation logic as the backend

-- Function to normalize and calculate risk score
CREATE OR REPLACE FUNCTION calculate_risk_score(temp DECIMAL, vib DECIMAL, load_pct DECIMAL)
RETURNS TABLE(risk_score DECIMAL, risk_level VARCHAR, reason VARCHAR) AS $$
DECLARE
    normalized_temp DECIMAL;
    normalized_vib DECIMAL;
    normalized_load DECIMAL;
    calculated_score DECIMAL;
    calculated_level VARCHAR(10);
    primary_reason VARCHAR(255);
BEGIN
    -- Normalize values to 0-100 scale
    normalized_temp := GREATEST(0, LEAST(100, (temp / 150.0) * 100));
    normalized_vib := GREATEST(0, LEAST(100, (vib / 50.0) * 100));
    normalized_load := load_pct; -- Already 0-100
    
    -- Calculate weighted risk score
    calculated_score := (0.4 * normalized_temp) + (0.35 * normalized_vib) + (0.25 * normalized_load);
    calculated_score := ROUND(calculated_score, 2);
    
    -- Determine risk level
    IF calculated_score >= 85 THEN
        calculated_level := 'CRITICAL';
    ELSIF calculated_score >= 65 THEN
        calculated_level := 'HIGH';
    ELSIF calculated_score >= 40 THEN
        calculated_level := 'MEDIUM';
    ELSE
        calculated_level := 'LOW';
    END IF;
    
    -- Determine primary contributing factor
    IF normalized_temp > normalized_vib AND normalized_temp > normalized_load THEN
        primary_reason := 'High temperature (' || temp || '°C)';
    ELSIF normalized_vib > normalized_load THEN
        primary_reason := 'Excessive vibration (' || vib || ' mm/s)';
    ELSE
        primary_reason := 'Heavy load (' || load_pct || '%)';
    END IF;
    
    RETURN QUERY SELECT calculated_score, calculated_level, primary_reason;
END;
$$ LANGUAGE plpgsql;

-- Insert risk events for all existing sensor logs
INSERT INTO risk_event (equipment_id, timestamp, risk_score, risk_level, reason)
SELECT 
    sl.equipment_id,
    sl.timestamp,
    calc.risk_score,
    calc.risk_level,
    calc.reason
FROM sensor_log sl
CROSS JOIN LATERAL calculate_risk_score(sl.temperature, sl.vibration, sl.load_percentage) calc
WHERE NOT EXISTS (
    SELECT 1 FROM risk_event re 
    WHERE re.equipment_id = sl.equipment_id 
    AND re.timestamp = sl.timestamp
)
ORDER BY sl.timestamp;

-- Show summary of inserted risk events
SELECT 
    risk_level,
    COUNT(*) as count
FROM risk_event
GROUP BY risk_level
ORDER BY 
    CASE risk_level
        WHEN 'CRITICAL' THEN 1
        WHEN 'HIGH' THEN 2
        WHEN 'MEDIUM' THEN 3
        WHEN 'LOW' THEN 4
    END;

-- Show equipment count by latest risk level
SELECT 
    COALESCE(latest_risk.risk_level::text, 'NO_RISK') as risk_category,
    COUNT(DISTINCT e.id) as equipment_count
FROM equipment e
LEFT JOIN LATERAL (
    SELECT risk_level
    FROM risk_event re
    WHERE re.equipment_id = e.id
    ORDER BY re.timestamp DESC
    LIMIT 1
) latest_risk ON true
GROUP BY latest_risk.risk_level
ORDER BY 
    CASE latest_risk.risk_level
        WHEN 'CRITICAL' THEN 1
        WHEN 'HIGH' THEN 2
        WHEN 'MEDIUM' THEN 3
        WHEN 'LOW' THEN 4
        ELSE 5
    END;
