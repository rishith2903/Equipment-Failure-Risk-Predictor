import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { equipmentAPI } from '../api/equipmentAPI';
import './AddSensorLog.css';

const AddSensorLog = () => {
    const [searchParams] = useSearchParams();
    const [equipmentList, setEquipmentList] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
        formState: { errors },
        setValue
    } = useForm({
        defaultValues: {
            equipmentId: searchParams.get('equipmentId') || '',
            temperature: '',
            vibration: '',
            loadPercentage: '',
        }
    });

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

    const onSubmit = async (data) => {
        setError('');
        setLoading(true);

        try {
            const { equipmentId, ...logData } = data;
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

                <form onSubmit={handleSubmit(onSubmit)}>
                    <div className="form-group">
                        <label className="label">⚙️ Select Equipment *</label>
                        <select
                            {...register('equipmentId', {
                                required: 'Please select an equipment'
                            })}
                            className={`input ${errors.equipmentId ? 'input-error' : ''}`}
                        >
                            <option value="">Choose equipment...</option>
                            {equipmentList.map(eq => (
                                <option key={eq.id} value={eq.id}>
                                    {eq.name} ({eq.type})
                                </option>
                            ))}
                        </select>
                        {errors.equipmentId && (
                            <span className="error-message">{errors.equipmentId.message}</span>
                        )}
                    </div>

                    <div className="form-group">
                        <label className="label">🌡️ Temperature (°C) *</label>
                        <input
                            type="number"
                            {...register('temperature', {
                                required: 'Temperature is required',
                                min: {
                                    value: -50,
                                    message: 'Temperature must be at least -50°C'
                                },
                                max: {
                                    value: 200,
                                    message: 'Temperature cannot exceed 200°C'
                                },
                                valueAsNumber: true
                            })}
                            className={`input ${errors.temperature ? 'input-error' : ''}`}
                            placeholder="0 - 200"
                            step="0.01"
                        />
                        {errors.temperature && (
                            <span className="error-message">{errors.temperature.message}</span>
                        )}
                        {!errors.temperature && (
                            <small className="text-muted text-sm">Normal range: 0-150°C</small>
                        )}
                    </div>

                    <div className="form-group">
                        <label className="label">〰️ Vibration (mm/s) *</label>
                        <input
                            type="number"
                            {...register('vibration', {
                                required: 'Vibration is required',
                                min: {
                                    value: 0,
                                    message: 'Vibration must be at least 0 mm/s'
                                },
                                max: {
                                    value: 100,
                                    message: 'Vibration cannot exceed 100 mm/s'
                                },
                                valueAsNumber: true
                            })}
                            className={`input ${errors.vibration ? 'input-error' : ''}`}
                            placeholder="0 - 100"
                            step="0.01"
                        />
                        {errors.vibration && (
                            <span className="error-message">{errors.vibration.message}</span>
                        )}
                        {!errors.vibration && (
                            <small className="text-muted text-sm">Normal range: 0-50 mm/s</small>
                        )}
                    </div>

                    <div className="form-group">
                        <label className="label">⚡ Load Percentage (%) *</label>
                        <input
                            type="number"
                            {...register('loadPercentage', {
                                required: 'Load percentage is required',
                                min: {
                                    value: 0,
                                    message: 'Load must be at least 0%'
                                },
                                max: {
                                    value: 100,
                                    message: 'Load cannot exceed 100%'
                                },
                                valueAsNumber: true
                            })}
                            className={`input ${errors.loadPercentage ? 'input-error' : ''}`}
                            placeholder="0 - 100"
                            step="0.01"
                        />
                        {errors.loadPercentage && (
                            <span className="error-message">{errors.loadPercentage.message}</span>
                        )}
                        {!errors.loadPercentage && (
                            <small className="text-muted text-sm">Current operational load</small>
                        )}
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
