import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { equipmentAPI, riskAPI } from '../api/equipmentAPI';
import StatsCard from '../components/dashboard/StatsCard';
import EquipmentCard from '../components/equipment/EquipmentCard';
import RecentAlerts from '../components/dashboard/RecentAlerts';
import './Dashboard.css';

const Dashboard = () => {
    const [stats, setStats] = useState(null);
    const [equipmentList, setEquipmentList] = useState([]);
    const [riskDataMap, setRiskDataMap] = useState({});
    const [alerts, setAlerts] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        fetchDashboardData();
    }, []);

    const fetchDashboardData = async () => {
        try {
            // Fetch dashboard stats
            const statsRes = await riskAPI.getDashboardStats();
            setStats(statsRes.data);

            // Fetch equipment
            const equipRes = await equipmentAPI.getAll();
            setEquipmentList(equipRes.data);

            // Fetch risk data for each equipment
            const riskPromises = equipRes.data.map(eq =>
                riskAPI.getLatest(eq.id)
                    .then(res => ({ id: eq.id, data: res.data }))
                    .catch(() => ({ id: eq.id, data: null }))
            );
            const riskResults = await Promise.all(riskPromises);
            const riskMap = {};
            riskResults.forEach(r => {
                if (r.data) riskMap[r.id] = r.data;
            });
            setRiskDataMap(riskMap);

            // Fetch recent alerts
            const alertsRes = await riskAPI.getAlerts({ limit: 10 });
            setAlerts(alertsRes.data);

            setLoading(false);
        } catch (err) {
            console.error('Error fetching dashboard data:', err);
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="page-loading"><div className="spinner"></div></div>;
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
            <div className="dashboard-stats grid grid-cols-4 mb-xl">
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
