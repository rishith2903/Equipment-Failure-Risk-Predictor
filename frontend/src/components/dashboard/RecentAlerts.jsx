import React from 'react';
import RiskBadge from '../risk/RiskBadge';
import { formatDateTime } from '../../utils/dateUtils';
import './RecentAlerts.css';

const RecentAlerts = ({ alerts }) => {
    if (!alerts || alerts.length === 0) {
        return (
            <div className="recent-alerts card">
                <h3 className="alerts-title">Recent Alerts</h3>
                <div className="alerts-empty text-muted text-sm">
                    No recent alerts
                </div>
            </div>
        );
    }

    return (
        <div className="recent-alerts card">
            <h3 className="alerts-title">Recent Alerts</h3>
            <ul className="alerts-list">
                {alerts.slice(0, 5).map((alert, index) => (
                    <li key={index} className="alert-item">
                        <div className="alert-header">
                            <span className="alert-equipment">{alert.equipmentName}</span>
                            <RiskBadge level={alert.riskLevel} />
                        </div>
                        <p className="alert-reason text-sm text-muted">{alert.reason}</p>
                        <p className="alert-time text-sm text-muted">{formatDateTime(alert.timestamp)}</p>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default RecentAlerts;
