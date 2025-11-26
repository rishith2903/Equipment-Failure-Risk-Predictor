import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { equipmentAPI, riskAPI } from '../api/equipmentAPI';
import EquipmentCard from '../components/equipment/EquipmentCard';
import './EquipmentList.css';

const EquipmentList = () => {
    const [equipmentList, setEquipmentList] = useState([]);
    const [riskDataMap, setRiskDataMap] = useState({});
    const [search, setSearch] = useState('');
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        fetchEquipment();
    }, []);

    const fetchEquipment = async () => {
        try {
            const res = await equipmentAPI.getAll();
            setEquipmentList(res.data);

            const riskPromises = res.data.map(eq =>
                riskAPI.getLatest(eq.id)
                    .then(r => ({ id: eq.id, data: r.data }))
                    .catch(() => ({ id: eq.id, data: null }))
            );
            const results = await Promise.all(riskPromises);
            const map = {};
            results.forEach(r => { if (r.data) map[r.id] = r.data; });
            setRiskDataMap(map);

            setLoading(false);
        } catch (err) {
            console.error('Error:', err);
            setLoading(false);
        }
    };

    const filtered = equipmentList.filter(e =>
        e.name.toLowerCase().includes(search.toLowerCase()) ||
        e.type.toLowerCase().includes(search.toLowerCase())
    );

    if (loading) return <div className="page-loading"><div className="spinner"></div></div>;

    return (
        <div className="equipment-list-page fade-in">
            <div className="page-header">
                <div>
                    <h1 className="page-title">Equipment List</h1>
                    <p className="page-subtitle text-muted">Manage all your industrial equipment</p>
                </div>
                <button className="btn btn-primary" onClick={() => navigate('/add-equipment')}>
                    ➕ Add Equipment
                </button>
            </div>

            <div className="search-bar mb-lg">
                <input
                    type="text"
                    className="input"
                    placeholder="Search equipment by name or type..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                />
            </div>

            {filtered.length > 0 ? (
                <div className="grid grid-cols-3">
                    {filtered.map(equipment => (
                        <EquipmentCard
                            key={equipment.id}
                            equipment={equipment}
                            riskData={riskDataMap[equipment.id]}
                        />
                    ))}
                </div>
            ) : (
                <div className="empty-state card">
                    <p>{search ? 'No equipment found matching your search' : 'No equipment available'}</p>
                </div>
            )}
        </div>
    );
};

export default EquipmentList;
