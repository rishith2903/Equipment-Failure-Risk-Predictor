import { useQuery } from '@tanstack/react-query';
import { equipmentAPI, riskAPI } from '../api/equipmentAPI';

/**
 * Custom hook for fetching dashboard data using React Query
 * Fetches stats, equipment list, risk data, and alerts in parallel
 */
export const useDashboardData = () => {
    // Fetch dashboard stats
    const statsQuery = useQuery({
        queryKey: ['dashboard', 'stats'],
        queryFn: async () => {
            const response = await riskAPI.getDashboardStats();
            return response.data;
        },
    });

    // Fetch equipment list
    const equipmentQuery = useQuery({
        queryKey: ['equipment', 'list'],
        queryFn: async () => {
            const response = await equipmentAPI.getAll();
            return response.data;
        },
    });

    // Fetch risk data for each equipment
    const riskDataQuery = useQuery({
        queryKey: ['equipment', 'riskData'],
        queryFn: async () => {
            const equipmentList = equipmentQuery.data || [];
            const riskPromises = equipmentList.map(eq =>
                riskAPI.getLatest(eq.id)
                    .then(res => ({ id: eq.id, data: res.data }))
                    .catch(() => ({ id: eq.id, data: null }))
            );
            const riskResults = await Promise.all(riskPromises);
            const riskMap = {};
            riskResults.forEach(r => {
                if (r.data) riskMap[r.id] = r.data;
            });
            return riskMap;
        },
        enabled: !!equipmentQuery.data && equipmentQuery.data.length > 0,
    });

    // Fetch recent alerts
    const alertsQuery = useQuery({
        queryKey: ['alerts', 'recent'],
        queryFn: async () => {
            const response = await riskAPI.getAlerts({ limit: 10 });
            return response.data;
        },
    });

    // Combine all queries
    return {
        stats: statsQuery.data,
        equipmentList: equipmentQuery.data || [],
        riskDataMap: riskDataQuery.data || {},
        alerts: alertsQuery.data || [],
        isLoading: statsQuery.isLoading || equipmentQuery.isLoading || alertsQuery.isLoading,
        error: statsQuery.error || equipmentQuery.error || riskDataQuery.error || alertsQuery.error,
    };
};
