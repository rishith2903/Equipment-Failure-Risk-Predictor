import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { equipmentAPI, riskAPI } from '../api/equipmentAPI';
import RiskBadge from '../components/risk/RiskBadge';
import { formatDate, formatDateTime } from '../utils/dateUtils';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import './EquipmentDetail.css';

const EquipmentDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [equipment, setEquipment] = useState(null);
    const [currentRisk, setCurrentRisk] = useState(null);
    const [riskHistory, setRiskHistory] = useState([]);
    const [sensorLogs, setSensorLogs] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchData();
    }, [id]);

    const fetchData = async () => {
        try {
            const [eqRes, riskRes, historyRes, logsRes] = await Promise.all([
                equipmentAPI.getById(id),
                riskAPI.getLatest(id).catch(() => ({ data: null })),
                riskAPI.getHistory(id, { limit: 20 }).catch(() => ({ data: [] })),
                equipmentAPI.getLogs(id, { limit: 20 }).catch(() => ({ data: [] })),
            ]);

            setEquipment(eqRes.data);
            setCurrentRisk(riskRes.data);
            setRiskHistory(historyRes.data);
            setSensorLogs(logsRes.data);
            setLoading(false);
        } catch (err) {
            console.error('Error:', err);
            setLoading(false);
        }
    };

    if (loading) return <div className="page-loading"><div className="spinner"></div></div>;
    if (!equipment) return <div className="alert alert-error">Equipment not found</div>;

    const chartData = sensorLogs.map(log => ({
        time: new Date(log.timestamp).toLocaleTimeString(),
        temp: log.temperature,
        vib: log.vibration,
        load: log.loadPercentage,
    })).reverse();

    const riskChartData = riskHistory.map(r => ({
        time: new Date(r.timestamp).toLocaleTimeString(),
        score: r.riskScore,
    })).reverse();

    return (
        <div className="equipment-detail-page fade-in">
            <div className="page-header">
                <div>
                    <button className="btn btn-secondary mb-sm" onClick={() => navigate('/equipment')}>
                        ← Back
                    </button>
                    <h1 className="page-title">{equipment.name}</h1>
                    <p className="page-subtitle text-muted">{equipment.type}</p>
                </div>
                {currentRisk && <RiskBadge level={currentRisk.riskLevel} />}
            </div>

            <div className="detail-grid">
                <div className="info-section card">
                    <h3 className="section-title">Equipment Information</h3>
                    <div className="info-grid">
                        <div className="info-item">
                            <span className="info-label text-sm text-muted">Location</span>
                            <span className="info-value">{equipment.location || 'N/A'}</span>
                        </div>
                        <div className="info-item">
                            <span className="info-label text-sm text-muted">Install Date</span>
                            <span className="info-value">{formatDate(equipment.installDate)}</span>
                        </div>
                        {currentRisk && (
                            <>
                                <div className="info-item">
                                    <span className="info-label text-sm text-muted">Risk Score</span>
                                    <span className="info-value risk-score">{currentRisk.riskScore}</span>
                                </div>
                                <div className="info-item">
                                    <span className="info-label text-sm text-muted">Primary Reason</span>
                                    <span className="info-value text-sm">{currentRisk.reason}</span>
                                </div>
                            </>
                        )}
                    </div>
                </div>

                {chartData.length > 0 && (
                    <div className="chart-section card">
                        <h3 className="section-title">Sensor Metrics</h3>
                        <ResponsiveContainer width="100%" height={250}>
                            <LineChart data={chartData}>
                                <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                                <XAxis dataKey="time" stroke="var(--color-text-muted)" />
                                <YAxis stroke="var(--color-text-muted)" />
                                <Tooltip
                                    contentStyle={{ background: 'var(--color-surface)', border: '1px solid var(--color-border)' }}
                                />
                                <Legend />
                                <Line type="monotone" dataKey="temp" stroke="var(--color-high)" name="Temperature" />
                                <Line type="monotone" dataKey="vib" stroke="var(--color-medium)" name="Vibration" />
                                <Line type="monotone" dataKey="load" stroke="var(--color-primary)" name="Load" />
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                )}

                {riskChartData.length > 0 && (
                    <div className="chart-section card">
                        <h3 className="section-title">Risk Score Timeline</h3>
                        <ResponsiveContainer width="100%" height={250}>
                            <LineChart data={riskChartData}>
                                <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                                <XAxis dataKey="time" stroke="var(--color-text-muted)" />
                                <YAxis stroke="var(--color-text-muted)" />
                                <Tooltip
                                    contentStyle={{ background: 'var(--color-surface)', border: '1px solid var(--color-border)' }}
                                />
                                <Line type="monotone" dataKey="score" stroke="var(--color-critical)" name="Risk Score" />
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                )}

                {sensorLogs.length > 0 && (
                    <div className="logs-section card">
                        <h3 className="section-title">Recent Sensor Logs</h3>
                        <div className="table-container">
                            <table className="logs-table">
                                <thead>
                                    <tr>
                                        <th>Timestamp</th>
                                        <th>Temperature</th>
                                        <th>Vibration</th>
                                        <th>Load %</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {sensorLogs.slice(0, 10).map((log, i) => (
                                        <tr key={i}>
                                            <td>{formatDateTime(log.timestamp)}</td>
                                            <td>{log.temperature}°C</td>
                                            <td>{log.vibration} mm/s</td>
                                            <td>{log.loadPercentage}%</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default EquipmentDetail;
