import { useState } from 'react';
import { roleApi } from '../../server/roles';

export const useRoles = () => {
    const [roles, setRoles] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const fetchRoles = async () => {
        setLoading(true);
        setError(null);
        try {
            const roles = await roleApi.getAllRoles();
            setRoles(roles);
            return roles;
        } catch (err) {
            setError('Roller yüklenirken hata oluştu');
            return [];
        } finally {
            setLoading(false);
        }
    };

    return {
        roles,
        loading,
        error,
        fetchRoles
    };
};