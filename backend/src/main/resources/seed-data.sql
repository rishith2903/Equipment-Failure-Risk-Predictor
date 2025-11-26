-- Sample seed data for Equipment Failure Risk Predictor

-- Insert sample equipment
INSERT INTO equipment (name, type, location, install_date, notes) VALUES
('Motor A-101', 'Motor', 'Building A - Floor 1', '2022-01-15', 'Primary production line motor'),
('Pump B-205', 'Pump', 'Building B - Floor 2', '2021-06-20', 'Coolant circulation pump'),
('Compressor C-303', 'Compressor', 'Building C - Floor 3', '2020-03-10', 'Main air compressor'),
('Turbine T-404', 'Turbine', 'Power Plant', '2019-11-05', 'Steam turbine generator'),
('Motor A-102', 'Motor', 'Building A - Floor 1', '2022-08-01', 'Secondary production line motor'),
('Pump B-206', 'Pump', 'Building B - Floor 2', '2021-12-15', 'Water supply pump'),
('Compressor C-304', 'Compressor', 'Building C - Floor 3', '2020-09-25', 'Backup air compressor'),
('Generator G-501', 'Generator', 'Power Plant', '2018-05-12', 'Emergency backup generator'),
('Motor A-103', 'Motor', 'Building A - Floor 1', '2023-02-14', 'Assembly line motor'),
('Conveyor CV-601', 'Conveyor', 'Warehouse', '2021-07-30', 'Material handling conveyor');

-- Insert sensor logs with varying risk levels
-- LOW RISK - Motor A-101 (Normal operation)
INSERT INTO sensor_log (equipment_id, timestamp, temperature, vibration, load_percentage) VALUES
(1, NOW() - INTERVAL '5 hours', 45.5, 3.2, 35.0),
(1, NOW() - INTERVAL '4 hours', 46.2, 3.5, 37.5),
(1, NOW() - INTERVAL '3 hours', 44.8, 3.1, 33.0),
(1, NOW() - INTERVAL '2 hours', 47.1, 3.8, 38.5),
(1, NOW() - INTERVAL '1 hour', 45.9, 3.3, 36.0),
(1, NOW(), 46.5, 3.4, 35.5);

-- MEDIUM RISK - Pump B-205 (Elevated temperature)
INSERT INTO sensor_log (equipment_id, timestamp, temperature, vibration, load_percentage) VALUES
(2, NOW() - INTERVAL '5 hours', 65.2, 5.1, 42.0),
(2, NOW() - INTERVAL '4 hours', 67.8, 5.4, 45.5),
(2, NOW() - INTERVAL '3 hours', 66.5, 5.2, 43.8),
(2, NOW() - INTERVAL '2 hours', 68.9, 5.6, 46.2),
(2, NOW() - INTERVAL '1 hour', 69.5, 5.7, 47.0),
(2, NOW(), 70.2, 5.8, 48.5);

-- HIGH RISK - Compressor C-303 (High vibration and temperature)
INSERT INTO sensor_log (equipment_id, timestamp, temperature, vibration, load_percentage) VALUES
(3, NOW() - INTERVAL '5 hours', 85.3, 12.5, 55.0),
(3, NOW() - INTERVAL '4 hours', 87.9, 13.2, 58.5),
(3, NOW() - INTERVAL '3 hours', 86.7, 12.8, 56.8),
(3, NOW() - INTERVAL '2 hours', 89.4, 14.1, 62.0),
(3, NOW() - INTERVAL '1 hour', 91.2, 14.8, 65.5),
(3, NOW(), 93.5, 15.3, 68.2);

-- CRITICAL RISK - Turbine T-404 (All metrics elevated)
INSERT INTO sensor_log (equipment_id, timestamp, temperature, vibration, load_percentage) VALUES
(4, NOW() - INTERVAL '5 hours', 125.5, 28.5, 85.0),
(4, NOW() - INTERVAL '4 hours', 128.9, 30.2, 88.5),
(4, NOW() - INTERVAL '3 hours', 127.3, 29.8, 87.2),
(4, NOW() - INTERVAL '2 hours', 131.7, 32.1, 91.5),
(4, NOW() - INTERVAL '1 hour', 135.2, 34.5, 94.8),
(4, NOW(), 138.7, 36.2, 97.5);

-- Motor A-102 - MEDIUM RISK (High load)
INSERT INTO sensor_log (equipment_id, timestamp, temperature, vibration, load_percentage) VALUES
(5, NOW() - INTERVAL '3 hours', 55.2, 4.5, 72.0),
(5, NOW() - INTERVAL '2 hours', 57.8, 4.8, 75.5),
(5, NOW() - INTERVAL '1 hour', 56.5, 4.6, 73.8),
(5, NOW(), 58.9, 4.9, 76.2);

-- Pump B-206 - LOW RISK
INSERT INTO sensor_log (equipment_id, timestamp, temperature, vibration, load_percentage) VALUES
(6, NOW() - INTERVAL '2 hours', 48.5, 3.8, 40.0),
(6, NOW() - INTERVAL '1 hour', 49.2, 4.0, 42.5),
(6, NOW(), 48.8, 3.9, 41.2);

-- Compressor C-304 - HIGH RISK (High vibration)
INSERT INTO sensor_log (equipment_id, timestamp, temperature, vibration, load_percentage) VALUES
(7, NOW() - INTERVAL '2 hours', 78.5, 18.5, 62.0),
(7, NOW() - INTERVAL '1 hour', 82.3, 20.2, 65.5),
(7, NOW(), 85.7, 22.1, 68.8);

-- Generator G-501 - MEDIUM RISK
INSERT INTO sensor_log (equipment_id, timestamp, temperature, vibration, load_percentage) VALUES
(8, NOW() - INTERVAL '2 hours', 72.5, 8.5, 55.0),
(8, NOW() - INTERVAL '1 hour', 74.8, 9.2, 58.5),
(8, NOW(), 76.2, 9.8, 61.2);

-- Motor A-103 - LOW RISK
INSERT INTO sensor_log (equipment_id, timestamp, temperature, vibration, load_percentage) VALUES
(9, NOW() - INTERVAL '1 hour', 42.5, 2.8, 32.0),
(9, NOW(), 43.2, 2.9, 33.5);

-- Conveyor CV-601 - LOW RISK
INSERT INTO sensor_log (equipment_id, timestamp, temperature, vibration, load_percentage) VALUES
(10, NOW() - INTERVAL '1 hour', 38.5, 2.2, 28.0),
(10, NOW(), 39.1, 2.3, 29.5);
