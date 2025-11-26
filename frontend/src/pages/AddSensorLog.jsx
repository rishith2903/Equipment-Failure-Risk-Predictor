import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { equipmentAPI } from '../api/equipmentAPI';
import './AddSensorLog.css';

const AddSensorLog = () => {
    const [searchParams] = useSearchParams();
    const [equipmentList, setEquipmentList] = useState([]);
    const [formData, setFormData] = useState({
        equipmentId: searchParams.get('equipmentId') || '',
        temperature: '',
        vibration: '',
        loadPercentage: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        fetchEquipment();
    }, []);

    const fetchEquipment = async () => {
        try {
            const res = await equipmentAPI.getAll();
            setEquipmentList(res.data);
        } catch (err) {
            console.error('Error fetching equipment:', err);
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const { equipmentId, ...logData } = formData;
            await equipmentAPI.addLog(equipmentId, {
                temperature: parseFloat(logData.temperature),
                vibration: parseFloat(logData.vibration),
                loadPercentage: parseFloat(logData.loadPercentage),
            });
            navigate(`/equipment/${equipmentId}`);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to add sensor log');
            setLoading(false);
        }
    };

    return (
        <div className="add-sensor-log-page fade-in">
            <div className="page-header">
                <div>
                    <button className="btn btn-secondary mb-sm" onClick={() => navigate(-1)}>
                        ← Back
                    </button>
                    <h1 className="page-title">📝 Add Sensor Log</h1>
                    <p className="page-subtitle text-muted">📊 Record new sensor readings</p>
                </div>
            </div>

            <div className="form-container card">
                {error && <div className="alert alert-error">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="label">⚙️ Select Equipment *</label>
                        <select
                            name="equipmentId"
                            className="input"
                            value={formData.equipmentId}
                            onChange={handleChange}
                            required
                        >
                            <option value="">Choose equipment...</option>
                            {equipmentList.map(eq => (
                                <option key={eq.id} value={eq.id}>
                                    {eq.name} ({eq.type})
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label className="label">🌡️ Temperature (°C) *</label>
                        <input
                            type="number"
                            name="temperature"
                            className="input"
                            placeholder="0 - 200"
                            step="0.01"
                            min="-50"
                            max="200"
                            value={formData.temperature}
                            onChange={handleChange}
                            required
                        />
                        <small className="text-muted text-sm">Normal range: 0-150°C</small>
                    </div>

                    <div className="form-group">
                        <label className="label">〰️ Vibration (mm/s) *</label>
                        <input
                            type="number"
                            name="vibration"
                            className="input"
                            placeholder="0 - 100"
                            step="0.01"
                            min="0"
                            max="100"
                            value={formData.vibration}
                            onChange={handleChange}
                            required
                        />
                        <small className="text-muted text-sm">Normal range: 0-50 mm/s</small>
                    </div>

                    <div className="form-group">
                        <label className="label">⚡ Load Percentage (%) *</label>
                        <input
                            type="number"
                            name="loadPercentage"
                            className="input"
                            placeholder="0 - 100"
                            step="0.01"
                            min="0"
                            max="100"
                            value={formData.loadPercentage}
                            onChange={handleChange}
                            required
                        />
                        <small className="text-muted text-sm">Current operational load</small>
                    </div>

                    <div className="form-actions">
                        <button type="button" className="btn btn-secondary" onClick={() => navigate(-1)}>
                            ❌ Cancel
                        </button>
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? '⏳ Adding...' : '✅ Add Sensor Log'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AddSensorLog;
