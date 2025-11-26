import React from 'react';
import './StatsCard.css';

const StatsCard = ({ label, value, icon, variant = 'default' }) => {
    return (
        <div className={`stats-card card ${variant}`}>
            <div className="stats-header">
                <span className="stats-label text-sm text-muted">{label}</span>
                <div className="stats-icon">{icon}</div>
            </div>
            <div className="stats-value">{value}</div>
        </div>
    );
};

export default StatsCard;
