-- Equipment Failure Risk Predictor - Initial Database Schema

-- Table: equipment
CREATE TABLE equipment (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    location VARCHAR(255),
    install_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: sensor_log
CREATE TABLE sensor_log (
    id BIGSERIAL PRIMARY KEY,
    equipment_id BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    temperature DECIMAL(5,2) NOT NULL CHECK (temperature >= -50 AND temperature <= 200),
    vibration DECIMAL(5,2) NOT NULL CHECK (vibration >= 0 AND vibration <= 100),
    load_percentage DECIMAL(5,2) NOT NULL CHECK (load_percentage >= 0 AND load_percentage <= 100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
);

-- Table: risk_event
CREATE TABLE risk_event (
    id BIGSERIAL PRIMARY KEY,
    equipment_id BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    risk_score DECIMAL(5,2) NOT NULL CHECK (risk_score >= 0 AND risk_score <= 100),
    risk_level VARCHAR(20) NOT NULL CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    reason TEXT,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
);

-- Indexes for performance optimization
CREATE INDEX idx_sensor_log_equipment_timestamp ON sensor_log(equipment_id, timestamp DESC);
CREATE INDEX idx_risk_event_equipment_timestamp ON risk_event(equipment_id, timestamp DESC);
CREATE INDEX idx_risk_event_level ON risk_event(risk_level);
CREATE INDEX idx_equipment_type ON equipment(type);

-- Comments for documentation
COMMENT ON TABLE equipment IS 'Stores industrial equipment information';
COMMENT ON TABLE sensor_log IS 'Stores sensor readings from equipment';
COMMENT ON TABLE risk_event IS 'Stores calculated risk events and alerts';
COMMENT ON COLUMN sensor_log.temperature IS 'Temperature in Celsius';
COMMENT ON COLUMN sensor_log.vibration IS 'Vibration in mm/s';
COMMENT ON COLUMN sensor_log.load_percentage IS 'Load percentage (0-100)';
