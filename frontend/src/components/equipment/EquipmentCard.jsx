import React from 'react';
import { useNavigate } from 'react-router-dom';
import RiskBadge from '../risk/RiskBadge';
import { formatDate } from '../../utils/dateUtils';
import './EquipmentCard.css';

const EquipmentCard = ({ equipment, riskData }) => {
    const navigate = useNavigate();

    return (
        <div className="equipment-card card" onClick={() => navigate(`/equipment/${equipment.id}`)}>
            <div className="equipment-card-header">
                <div>
                    <h3 className="equipment-name">{equipment.name}</h3>
                    <p className="equipment-type text-muted text-sm">{equipment.type}</p>
                </div>
                {riskData && <RiskBadge level={riskData.riskLevel} />}
            </div>

            <div className="equipment-card-body">
                <div className="equipment-info">
                    <p className="text-sm text-muted">
                        📍 {equipment.location || 'No location'}
                    </p>
                    <p className="text-sm text-muted">
                        📅 Installed: {formatDate(equipment.installDate)}
                    </p>
                </div>

                {riskData && (
                    <div className="equipment-risk-score">
                        <span className="text-sm text-muted">Risk Score:</span>
                        <span className="risk-score-value">{riskData.riskScore}</span>
                    </div>
                )}
            </div>

            <div className="equipment-card-footer">
                <span className="view-details-link">View Details →</span>
            </div>
        </div>
    );
};

export default EquipmentCard;
