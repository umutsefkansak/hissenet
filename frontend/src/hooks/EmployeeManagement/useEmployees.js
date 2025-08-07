import { useState, useEffect, useCallback } from 'react';
import {
    getAllEmployees,
    getAllEmployeesPageable,
    createEmployee,
    updateEmployee,
    getEmployee,
    deleteEmployee
} from '../../server/employees';

const useEmployees = (usePagination = false) => {
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const [paginationData, setPaginationData] = useState({
        content: [],
        totalElements: 0,
        totalPages: 0,
        number: 0,
        size: 10,
        first: true,
        last: false,
        numberOfElements: 0
    });

    const [paginationParams, setPaginationParams] = useState({
        page: 0,
        size: 10,
        sortBy: 'id',
        sortDir: 'asc',
        searchTerm: '',
        searchField: 'all'
    });

    const filterEmployees = useCallback((employees, searchTerm, searchField) => {
        if (!searchTerm || !employees) return employees;

        const terms = searchTerm.toLowerCase().trim().split(/\s+/).filter(term => term.length > 0);

        if (terms.length === 0) return employees;

        return employees.filter(employee => {
            const searchableFields = [];

            switch (searchField) {
                case 'firstName':
                    searchableFields.push(employee.firstName?.toLowerCase() || '');
                    break;
                case 'lastName':
                    searchableFields.push(employee.lastName?.toLowerCase() || '');
                    break;
                case 'email':
                    searchableFields.push(employee.email?.toLowerCase() || '');
                    break;
                case 'phone':
                    searchableFields.push(employee.phone || '');
                    break;
                case 'position':
                    searchableFields.push(employee.position?.toLowerCase() || '');
                    break;
                case 'all':
                default:
                    searchableFields.push(
                        employee.firstName?.toLowerCase() || '',
                        employee.lastName?.toLowerCase() || '',
                        employee.email?.toLowerCase() || '',
                        employee.phone || '',
                        employee.position?.toLowerCase() || ''
                    );
                    break;
            }

            const fullName = `${employee.firstName?.toLowerCase() || ''} ${employee.lastName?.toLowerCase() || ''}`.trim();
            searchableFields.push(fullName);

            return terms.every(term =>
                searchableFields.some(field => field.includes(term))
            );
        });
    }, []);

    const fetchEmployees = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await getAllEmployees();
            if (response.success) {
                let data = response.data.data || [];

                if (paginationParams.searchTerm) {
                    data = filterEmployees(data, paginationParams.searchTerm, paginationParams.searchField);
                }

                setEmployees(data);
            } else {
                setError(response.error);
            }
        } catch (err) {
            console.error('Fetch employees error:', err);
            setError('Personeller yüklenirken hata oluştu');
        } finally {
            setLoading(false);
        }
    }, [paginationParams.searchTerm, paginationParams.searchField, filterEmployees]);

    const fetchEmployeesPageable = useCallback(async (params = paginationParams) => {
        setLoading(true);
        setError(null);
        try {
            const response = await getAllEmployeesPageable({
                page: params.page,
                size: params.size,
                sortBy: params.sortBy,
                sortDir: params.sortDir,
                search: params.searchTerm,
                searchField: params.searchField
            });

            if (response.success) {
                let data = response.data.data;

                if (params.searchTerm && data.content) {
                    const filteredContent = filterEmployees(data.content, params.searchTerm, params.searchField);

                    const startIndex = params.page * params.size;
                    const endIndex = startIndex + params.size;
                    const paginatedContent = filteredContent.slice(startIndex, endIndex);

                    data = {
                        ...data,
                        content: paginatedContent,
                        totalElements: filteredContent.length,
                        totalPages: Math.ceil(filteredContent.length / params.size),
                        numberOfElements: paginatedContent.length,
                        number: params.page,
                        size: params.size,
                        first: params.page === 0,
                        last: params.page >= Math.ceil(filteredContent.length / params.size) - 1
                    };
                }

                setPaginationData(data);
            } else {
                setError(response.error);
            }
        } catch (err) {
            console.error('Fetch employees pageable error:', err);
            setError('Personeller yüklenirken hata oluştu');
        } finally {
            setLoading(false);
        }
    }, [filterEmployees]);

    const handlePageChange = useCallback((newPage) => {
        const newParams = { ...paginationParams, page: newPage };
        setPaginationParams(newParams);
    }, [paginationParams]);

    const handlePageSizeChange = useCallback((newSize, newPage = 0) => {
        const newParams = {
            ...paginationParams,
            size: newSize,
            page: newPage
        };
        setPaginationParams(newParams);
    }, [paginationParams]);

    const handleSortChange = useCallback((sortBy, sortDir = 'asc') => {
        const newParams = {
            ...paginationParams,
            sortBy,
            sortDir,
            page: 0
        };
        setPaginationParams(newParams);
    }, [paginationParams]);

    const handleSearch = useCallback((searchTerm, searchField = 'all') => {
        const newParams = {
            ...paginationParams,
            searchTerm: searchTerm || '',
            searchField,
            page: 0
        };

        setPaginationParams(newParams);
    }, [paginationParams]);

    const handleClearSearch = useCallback(() => {
        const newParams = {
            ...paginationParams,
            searchTerm: '',
            searchField: 'all',
            page: 0
        };
        setPaginationParams(newParams);
    }, [paginationParams]);

    const handleCreateEmployee = async (employeeData) => {
        setLoading(true);
        setError(null);
        try {
            const response = await createEmployee(employeeData);
            if (response.success) {
                return { success: true, data: response.data };
            } else {
                setError(response.error);
                return { success: false, error: response.error };
            }
        } catch (err) {
            console.error('Create employee error:', err);
            const errorMsg = 'Personel oluşturulurken hata oluştu';
            setError(errorMsg);
            return { success: false, error: errorMsg };
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateEmployee = async (employeeData) => {
        setLoading(true);
        setError(null);
        try {
            const response = await updateEmployee(employeeData);
            if (response.success) {
                return { success: true, data: response.data };
            } else {
                setError(response.error);
                return { success: false, error: response.error };
            }
        } catch (err) {
            console.error('Update employee error:', err);
            const errorMsg = 'Personel güncellenirken hata oluştu';
            setError(errorMsg);
            return { success: false, error: errorMsg };
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteEmployee = async (id) => {
        setLoading(true);
        setError(null);
        try {
            const response = await deleteEmployee(id);
            if (response.success) {
                if (usePagination) {
                    const currentPageData = paginationData;
                    if (currentPageData.numberOfElements === 1 && currentPageData.number > 0) {
                        const newParams = { ...paginationParams, page: currentPageData.number - 1 };
                        setPaginationParams(newParams);
                    } else {
                        setPaginationParams(prev => ({ ...prev }));
                    }
                } else {
                    setPaginationParams(prev => ({ ...prev }));
                }
                return { success: true };
            } else {
                setError(response.error);
                return { success: false, error: response.error };
            }
        } catch (err) {
            console.error('Delete employee error:', err);
            const errorMsg = 'Personel silinirken hata oluştu';
            setError(errorMsg);
            return { success: false, error: errorMsg };
        } finally {
            setLoading(false);
        }
    };

    const handleGetEmployee = async (id) => {
        setLoading(true);
        setError(null);
        try {
            const response = await getEmployee(id);
            if (response.success) {
                return { success: true, data: response.data.data };
            } else {
                setError(response.error);
                return { success: false, error: response.error };
            }
        } catch (err) {
            console.error('Get employee error:', err);
            const errorMsg = 'Personel bilgileri alınırken hata oluştu';
            setError(errorMsg);
            return { success: false, error: errorMsg };
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (usePagination) {
            fetchEmployeesPageable(paginationParams);
        } else {
            fetchEmployees();
        }
    }, [usePagination, paginationParams, fetchEmployeesPageable, fetchEmployees]);

    return {
        employees,
        paginationData,
        paginationParams,
        loading,
        error,
        fetchEmployees,
        fetchEmployeesPageable,
        createEmployee: handleCreateEmployee,
        updateEmployee: handleUpdateEmployee,
        deleteEmployee: handleDeleteEmployee,
        getEmployee: handleGetEmployee,
        handlePageChange,
        handlePageSizeChange,
        handleSortChange,
        handleSearch,
        handleClearSearch
    };
};

export default useEmployees;