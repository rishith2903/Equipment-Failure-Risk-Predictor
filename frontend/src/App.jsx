import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { QueryClientProvider } from '@tanstack/react-query';
import { queryClient } from './lib/queryClient';
import Layout from './components/layout/Layout';
import Dashboard from './pages/Dashboard';
import EquipmentList from './pages/EquipmentList';
import EquipmentDetail from './pages/EquipmentDetail';
import AddEquipment from './pages/AddEquipment';
import AddSensorLog from './pages/AddSensorLog';
import './index.css';

function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Layout />}>
                        <Route index element={<Dashboard />} />
                        <Route path="equipment" element={<EquipmentList />} />
                        <Route path="equipment/:id" element={<EquipmentDetail />} />
                        <Route path="add-equipment" element={<AddEquipment />} />
                        <Route path="add-log" element={<AddSensorLog />} />
                    </Route>
                </Routes>
            </BrowserRouter>
        </QueryClientProvider>
    );
}

export default App;

