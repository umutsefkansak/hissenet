import React, { useEffect,useState } from 'react';
import useEmployees from '../../hooks/EmployeeManagement/useEmployees';
import EmployeeTable from '../../components/employeeManagement/EmployeeTable';
import EmployeeForm from '../../components/employeeManagement/EmployeeForm';
import EmployeeModal from '../../components/employeeManagement/EmployeeModal';
import SearchInput from '../../components/common/SearchInput/SearchInput';
import './EmployeeManagementPage.css';
import Pagination from "../../components/common/Pagination/Pagination";
import DashboardCard from '../../components/common/Card/DashboardCard';
import { orderApi } from '../../services/api/orderApi';



const EmployeeManagementPage = () => {
    const {
        paginationData,
        paginationParams,
        loading,
        createEmployee,
        updateEmployee,
        deleteEmployee,
        handlePageChange,
        handlePageSizeChange,
        handleSearch,
        handleClearSearch
    } = useEmployees(true);

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedEmployee, setSelectedEmployee] = useState(null);
    const [isEdit, setIsEdit] = useState(false);
    const [todayTradeVolume, setTodayTradeVolume] = useState(null);

    useEffect(() => {
        const fetchTodayTradeVolume = async () => {
            try {
                const result = await orderApi.getTodayTotalTradeVolume();
                setTodayTradeVolume(`${Number(result.data).toLocaleString('tr-TR')}₺`);
            } catch (err) {
                console.error("İşlem hacmi alınamadı:", err);
                setTodayTradeVolume('₺0');
            }
        };

        fetchTodayTradeVolume();
    }, []);


    const dashboardData = {
        totalRevenue: todayTradeVolume ?? 'Yükleniyor...',
        totalEmployees: paginationData.totalElements || 0,
        dailyTransactions: (paginationData.content || []).filter(emp => emp.status === 'ACTIVE').length
    };

    const searchFilterOptions = [
        { value: 'all', label: 'Tümü' },
        { value: 'firstName', label: 'Ad' },
        { value: 'lastName', label: 'Soyad' },
        { value: 'email', label: 'E-posta' },
        { value: 'phone', label: 'Telefon' },
        { value: 'position', label: 'Pozisyon' }
    ];

    const handleAddEmployee = () => {
        setSelectedEmployee(null);
        setIsEdit(false);
        setIsModalOpen(true);
    };

    const handleEditEmployee = (employee) => {
        setSelectedEmployee(employee);
        setIsEdit(true);
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setSelectedEmployee(null);
        setIsEdit(false);
    };

    const handleFormSubmit = async (formData) => {
        try {
            let result;
            if (isEdit) {
                result = await updateEmployee(formData);
            } else {
                result = await createEmployee(formData);
            }

            if (result.success) {
                handleCloseModal();
                window.showToast && window.showToast(
                    isEdit ? 'Personel başarıyla güncellendi!' : 'Personel başarıyla eklendi!',
                    'success',
                    3000
                );
            } else {
                const errorMessage = result.error || 'İşlem sırasında bir hata oluştu';
                window.showToast && window.showToast(
                    errorMessage,
                    'error',
                    5000
                );
                console.error('İşlem hatası:', result.error);
            }
        } catch (error) {
            console.error('Form submit error:', error);
            window.showToast && window.showToast(
                'İşlem sırasında beklenmeyen hata oluştu',
                'error',
                5000
            );
        }
    };

    const handleDeleteEmployee = async (employeeId) => {
        if (window.confirm('Bu personeli silmek istediğinizden emin misiniz?')) {
            const result = await deleteEmployee(employeeId);
            if (result.success) {
                window.showToast && window.showToast('Personel başarıyla silindi!', 'success', 3000);
            } else {
                window.showToast && window.showToast(
                    result.error || 'Personel silinirken hata oluştu',
                    'error',
                    3000
                );
            }
        }
    };

    const handleManualSearch = (searchTerm, selectedFilter) => {
        handleSearch(searchTerm, selectedFilter);
    };

    const handleSearchClear = () => {
        handleClearSearch();
    };

    const handleSearchFilter = (filterValue) => {
        handleSearch(paginationParams.searchTerm, filterValue);
    };

    return (
        <div className="employee-management-page">
            <div className="page-header">
                <div className="header-content">
                    <h1 className="page-title">Personel Yönetimi</h1>
                    <button className="add-employee-btn" onClick={handleAddEmployee}>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M12 5V19M5 12H19" stroke="currentColor" strokeWidth="2" strokeLinecap="round"
                                  strokeLinejoin="round"/>
                        </svg>
                        Personel Ekle
                    </button>
                </div>
            </div>

            <div className="dashboard-cards">


                <DashboardCard
                    title="Günlük İşlem Hacmi"
                    icon={
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                             xmlns="http://www.w3.org/2000/svg">
                            <path d="M3 3V21H21" stroke="currentColor" strokeWidth="2" strokeLinecap="round"
                                  strokeLinejoin="round"/>
                            <path d="M9 9L12 6L16 10L20 6" stroke="currentColor" strokeWidth="2"
                                  strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                    }
                    value={dashboardData.totalRevenue}
                    subtitle="Son 24 saat"
                    iconVariant="trend-up"
                />

                <DashboardCard
                    title="Toplam Personel"
                    icon={
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                             xmlns="http://www.w3.org/2000/svg">
                            <path
                                d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21"
                                stroke="currentColor" strokeWidth="2" strokeLinecap="round"
                                strokeLinejoin="round"/>
                            <circle cx="9" cy="7" r="4" stroke="currentColor" strokeWidth="2"
                                    strokeLinecap="round" strokeLinejoin="round"/>
                            <path
                                d="M23 21V19C23 18.1326 22.7035 17.2982 22.1677 16.636C21.6319 15.9738 20.8918 15.5229 20.06 15.36"
                                stroke="currentColor" strokeWidth="2" strokeLinecap="round"
                                strokeLinejoin="round"/>
                            <path
                                d="M16 3.13C16.8318 3.29312 17.5719 3.74398 18.1077 4.40619C18.6435 5.06839 18.94 5.90285 18.94 6.77C18.94 7.63715 18.6435 8.47161 18.1077 9.13381C17.5719 9.79602 16.8318 10.2469 16 10.41"
                                stroke="currentColor" strokeWidth="2" strokeLinecap="round"
                                strokeLinejoin="round"/>
                        </svg>
                    }
                    value={dashboardData.totalEmployees}
                    subtitle="Aktif personeller"
                    iconVariant="users"
                />

                <div className="dashboard-card">
                    <div className="card-header">
                        <h3>Günlük Yapılan İşlem</h3>
                        <div className="card-icon trend-up">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                 xmlns="http://www.w3.org/2000/svg">
                                <path
                                    d="M16 21V19C16 17.9391 15.5786 16.9217 14.8284 16.1716C14.0783 15.4214 13.0609 15 12 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21"
                                    stroke="currentColor" strokeWidth="2" strokeLinecap="round"
                                    strokeLinejoin="round"/>
                                <circle cx="8.5" cy="7" r="4" stroke="currentColor" strokeWidth="2"
                                        strokeLinecap="round" strokeLinejoin="round"/>
                                <polyline points="17,11 19,13 23,9" stroke="currentColor" strokeWidth="2"
                                          strokeLinecap="round" strokeLinejoin="round"/>
                            </svg>
                        </div>
                    </div>
                    <div className="card-value">{dashboardData.dailyTransactions}</div>
                    <div className="card-subtitle">Son 24 saatte işlem yapan</div>
                </div>
            </div>

            <div className="search-bar-wrapper">
                <SearchInput
                    placeholder="Personel ara (Enter ile arayın)..."
                    value={paginationParams.searchTerm}
                    onSearch={handleManualSearch}
                    onClear={handleSearchClear}
                    onFilterChange={handleSearchFilter}
                    debounceMs={0}
                    showClearButton={true}
                    showSearchIcon={true}
                    searchFields={['Ad', 'Soyad', 'Email', 'Telefon', 'Pozisyon']}
                    filterOptions={searchFilterOptions}
                    showFilterDropdown={true}
                    multiField={true}
                    size="medium"
                    variant="outlined"
                />
            </div>

            <div className="table-section">
                <div className="table-header">
                    <h2>Personeller</h2>
                    <div className="table-actions">
                        <span className="table-subtitle">
                            {paginationParams.searchTerm ? (
                                `"${paginationParams.searchTerm}" için ${paginationData.totalElements} sonuç`
                            ) : (
                                `Toplam ${paginationData.totalElements} personel`
                            )}
                        </span>
                    </div>
                </div>

                <EmployeeTable
                    employees={paginationData.content || []}
                    onEdit={handleEditEmployee}
                    onDelete={handleDeleteEmployee}
                    loading={loading}
                />

                <Pagination
                    currentPage={paginationData.number || 0}
                    totalPages={paginationData.totalPages || 1}
                    totalElements={paginationData.totalElements || 0}
                    pageSize={paginationData.size || 10}
                    onPageChange={handlePageChange}
                    onPageSizeChange={handlePageSizeChange}
                    showPageSizeOptions={true}
                    showTotalElements={true}
                />
            </div>

            <EmployeeModal isOpen={isModalOpen} onClose={handleCloseModal}>
                <EmployeeForm
                    employee={selectedEmployee}
                    onSubmit={handleFormSubmit}
                    onCancel={handleCloseModal}
                    isEdit={isEdit}
                />
            </EmployeeModal>
        </div>
    );
};

export default EmployeeManagementPage;