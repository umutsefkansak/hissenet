import api  from './api';

export const roleApi = {
    getAllRoles: async () => {
        try {
            const response = await api.get('/roles');
            return response.data.data || [];
        } catch (error) {
            console.error('Roller alınırken hata:', error);
            return [];
        }
    },

    getActiveRoles: async () => {
        try {
            const response = await api.get('/roles/active');
            return response.data.data || [];
        } catch (error) {
            console.error('Aktif roller alınırken hata:', error);
            return [];
        }
    },

    updateEmployeeRoles: async (employeeId, roleIds) => {
        try {
            const response = await api.put(`/employees/${employeeId}/roles`, { roleIds });
            if (!response.data.success) {
                throw new Error(response.data.message || 'Roller güncellenirken hata oluştu');
            }
            return response.data;
        } catch (error) {
            console.error('Roller güncellenirken hata:', error);
            throw error;
        }
    }
};
