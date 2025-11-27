import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://equipment-failure-risk-predictor.onrender.com/api/v1' ;


const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const equipmentAPI = {
    // Equipment endpoints
    getAll: () => api.get('/equipment'),
    getById: (id) => api.get(`/equipment/${id}`),
    create: (data) => api.post('/equipment', data),
    update: (id, data) => api.put(`/equipment/${id}`, data),
    delete: (id) => api.delete(`/equipment/${id}`),

    // Sensor log endpoints
    getLogs: (equipmentId, params) => api.get(`/equipment/${equipmentId}/logs`, { params }),
    addLog: (equipmentId, data) => api.post(`/equipment/${equipmentId}/logs`, data),
};

export const riskAPI = {
    getLatest: (equipmentId) => api.get(`/equipment/${equipmentId}/risk/latest`),
    getHistory: (equipmentId, params) => api.get(`/equipment/${equipmentId}/risk/history`, { params }),
    getAlerts: (params) => api.get('/alerts', { params }),
    getDashboardStats: () => api.get('/dashboard/stats'),
};

export default api;
