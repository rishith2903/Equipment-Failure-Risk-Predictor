import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { equipmentAPI } from '../api/equipmentAPI';
import './AddEquipment.css';

const AddEquipment = () => {
    const [formData, setFormData] = useState({
        name: '',
        type: '',
        location: '',
        installDate: '',
        notes: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await equipmentAPI.create(formData);
            navigate('/equipment');
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create equipment');
            setLoading(false);
        }
    };

    return (
        <div className="add-equipment-page fade-in">
            <div className="page-header">
                <div>
                    <button className="btn btn-secondary mb-sm" onClick={() => navigate('/equipment')}>
                        ← Back
                    </button>
                    <h1 className="page-title">➕ Add New Equipment</h1>
                    <p className="page-subtitle text-muted">🏭 Register new industrial equipment</p>
                </div>
            </div>

            <div className="form-container card">
                {error && <div className="alert alert-error">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="label">📦 Equipment Name *</label>
                        <input
                            type="text"
                            name="name"
                            className="input"
                            placeholder="e.g., Motor A-101"
                            value={formData.name}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="label">🔧 Equipment Type *</label>
                        <select
                            name="type"
                            className="input"
                            value={formData.type}
                            onChange={handleChange}
                            required
                        >
                            <option value="">Select type...</option>
                            <option value="Motor">Motor</option>
                            <option value="Pump">Pump</option>
                            <option value="Compressor">Compressor</option>
                            <option value="Turbine">Turbine</option>
                            <option value="Conveyor">Conveyor</option>
                            <option value="Generator">Generator</option>
                            <option value="Other">Other</option>
                        </select>
                    </div>

                    <div className="form-group">
                        <label className="label">📍 Location</label>
                        <input
                            type="text"
                            name="location"
                            className="input"
                            placeholder="e.g., Building A - Floor 1"
                            value={formData.location}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group">
                        <label className="label">📅 Installation Date</label>
                        <input
                            type="date"
                            name="installDate"
                            className="input"
                            value={formData.installDate}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group">
                        <label className="label">📝 Notes</label>
                        <textarea
                            name="notes"
                            className="input"
                            rows="4"
                            placeholder="Additional information..."
                            value={formData.notes}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-actions">
                        <button type="button" className="btn btn-secondary" onClick={() => navigate('/equipment')}>
                            ❌ Cancel
                        </button>
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? '⏳ Creating...' : '✅ Create Equipment'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AddEquipment;
