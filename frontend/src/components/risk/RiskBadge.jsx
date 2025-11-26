import React from 'react';
import './RiskBadge.css';

const RiskBadge = ({ level }) => {
    if (!level) return null;

    const levelMap = {
        LOW: { label: 'Low', class: 'low' },
        MEDIUM: { label: 'Medium', class: 'medium' },
        HIGH: { label: 'High', class: 'high' },
        CRITICAL: { label: 'Critical', class: 'critical' },
    };

    const riskInfo = levelMap[level] || { label: level, class: 'low' };

    return (
        <span className={`risk-badge risk-badge-${riskInfo.class}`}>
            {riskInfo.label}
        </span>
    );
};

export default RiskBadge;
