import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDashboardData } from '../hooks/useDashboardData';
import { useWebSocket } from '../hooks/useWebSocket';
import StatsCard from '../components/dashboard/StatsCard';
import EquipmentCard from '../components/equipment/EquipmentCard';
import RecentAlerts from '../components/dashboard/RecentAlerts';
import './Dashboard.css';

const Dashboard = () => {
    const navigate = useNavigate();
    const { stats, equipmentList, riskDataMap, alerts, isLoading, error } = useDashboardData();
    const { messages: wsAlerts, isConnected } = useWebSocket('/topic/alerts');
    const [realtimeAlerts, setRealtimeAlerts] = useState([]);

    // Handle real-time WebSocket alerts
    useEffect(() => {
        if (wsAlerts.length > 0) {
            const latestAlert = wsAlerts[wsAlerts.length - 1];

            // Show toast notification
            showToast(latestAlert);

            // Add to realtime alerts list
            setRealtimeAlerts((prev) => [latestAlert, ...prev].slice(0, 5));
        }
    }, [wsAlerts]);

    const showToast = (alert) => {
        const riskColor = alert.riskLevel === 'CRITICAL' ? '#dc2626' : '#f59e0b';
        const emoji = alert.riskLevel === 'CRITICAL' ? '🚨' : '⚠️';

        // Create toast element
        const toast = document.createElement('div');
        toast.innerHTML = `
            <div style="
                position: fixed;
                top: 20px;
                right: 20px;
                background: ${riskColor};
                color: white;
                padding: 1rem 1.5rem;
                border-radius: 8px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.3);
                z-index: 9999;
                animation: slideIn 0.3s ease-out;
                max-width: 400px;
            ">
                <div style="font-weight: bold; margin-bottom: 0.25rem;">
                    ${emoji} ${alert.riskLevel} Risk Alert!
                </div>
                <div style="font-size: 0.9rem;">
                    ${alert.equipmentName}: ${alert.reason}
                </div>
            </div>
        `;

        document.body.appendChild(toast);

        // Remove after 5 seconds
        setTimeout(() => {
            toast.style.animation = 'slideOut 0.3s ease-in';
            setTimeout(() => toast.remove(), 300);
        }, 5000);
    };

    if (isLoading) {
        return <div className="page-loading"><div className="spinner"></div></div>;
    }

    if (error) {
        return (
            <div className="page-error">
                <h2>Error loading dashboard</h2>
                <p>{error.message || 'An unexpected error occurred'}</p>
                <button className="btn btn-primary" onClick={() => window.location.reload()}>
                    Retry
                </button>
            </div>
        );
    }

    return (
        <div className="dashboard-page fade-in">
            <div className="page-header">
                <div>
                    <h1 className="page-title">Dashboard</h1>
                    <p className="page-subtitle text-muted">Monitor equipment health and risk levels</p>
                </div>
                <button className="btn btn-primary" onClick={() => navigate('/add-log')}>
                    📝 Add Sensor Log
                </button>
            </div>

            {/* Statistics */}
            <div className="dashboard-stats grid grid-cols-5 mb-xl">
                <StatsCard
                    label="Total Equipment"
                    value={stats?.totalEquipment || 0}
                    icon="⚙️"
                    variant="primary"
                />
                <StatsCard
                    label="Critical Risk"
                    value={stats?.criticalEquipment || 0}
                    icon="🚨"
                    variant="critical"
                />
                <StatsCard
                    label="High Risk"
                    value={stats?.highRiskEquipment || 0}
                    icon="⚠️"
                    variant="high"
                />
                <StatsCard
                    label="Medium Risk"
                    value={stats?.mediumRiskEquipment || 0}
                    icon="🟡"
                    variant="medium"
                />
                <StatsCard
                    label="Healthy"
                    value={stats?.lowRiskEquipment || 0}
                    icon="✅"
                    variant="low"
                />
            </div>

            {/* Equipment Overview & Alerts */}
            <div className="dashboard-content">
                <div className="equipment-section">
                    <h2 className="section-title">Equipment Overview</h2>
                    {equipmentList.length > 0 ? (
                        <div className="grid grid-cols-3">
                            {equipmentList.slice(0, 6).map(equipment => (
                                <EquipmentCard
                                    key={equipment.id}
                                    equipment={equipment}
                                    riskData={riskDataMap[equipment.id]}
                                />
                            ))}
                        </div>
                    ) : (
                        <div className="empty-state card">
                            <p>No equipment found. Add equipment to get started.</p>
                            <button className="btn btn-primary mt-md" onClick={() => navigate('/add-equipment')}>
                                ➕ Add Equipment
                            </button>
                        </div>
                    )}
                </div>

                <div className="alerts-section">
                    <RecentAlerts alerts={alerts} />
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
