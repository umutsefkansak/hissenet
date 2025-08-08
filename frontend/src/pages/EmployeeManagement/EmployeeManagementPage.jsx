import React, { useEffect,useState } from 'react';
import useEmployees from '../../hooks/EmployeeManagement/useEmployees';
import EmployeeTable from '../../components/employeeManagement/EmployeeTable';
import EmployeeForm from '../../components/employeeManagement/EmployeeForm';
import EmployeeModal from '../../components/employeeManagement/EmployeeModal';
import SearchInput from '../../components/common/SearchInput/SearchInput';
import './EmployeeManagementPage.css';
import Pagination from "../../components/common/Pagination/Pagination";
import DashboardCard from '../../components/common/Card/DashboardCard';
import { orderApi } from '../../server/order';
import TrendArrow from '../../components/Icons/TrendArrow';
import UsersIconBlue from '../../components/Icons/UsersIconBlue';
import UserActivityIcon from '../../components/Icons/UserActivityIcon';
import PlusIcon from '../../components/Icons/PlusIcon';


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
    const [todayOrderCount, setTodayOrderCount] = useState(null);


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
        const fetchTodayOrderCount = async () => {
            try {
                const result = await orderApi.getTodayOrderCount();
                setTodayOrderCount(result.data || 0);
            } catch (err) {
                console.error("Bugünkü emir sayısı alınamadı:", err);
                setTodayOrderCount(0);
            }
        };


        fetchTodayTradeVolume();
        fetchTodayOrderCount();
    }, []);


    const dashboardData = {
        totalRevenue: todayTradeVolume ?? 'Yükleniyor...',
        totalEmployees: paginationData.totalElements || 0,
        dailyTransactions: todayOrderCount ?? 'Yükleniyor...'
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

    const handleDeleteEmployee = async (employee) => {
        const result = await deleteEmployee(employee.id);
        if (result.success) {
            window.showToast && window.showToast('Personel başarıyla silindi!', 'success', 3000);
        } else {
            window.showToast && window.showToast(
                result.error || 'Personel silinirken hata oluştu',
                'error',
                3000
            );
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
            <div className="employee-management-page-header">
                <div className="header-content">
                    <h1 className="page-title">Personel Yönetimi</h1>
                    <button className="add-employee-btn" onClick={handleAddEmployee}>
                        <PlusIcon className="mr-1" />
                        Personel Ekle
                    </button>
                </div>
            </div>

            <div className="dashboard-cards">


                <DashboardCard
                    title="Günlük İşlem Hacmi"
                    icon={
                       <TrendArrow/>
                    }
                    value={dashboardData.totalRevenue}
                    subtitle="Son 24 saat"
                    iconVariant="trend-up"
                />

                <DashboardCard
                    title="Toplam Personel"
                    icon={
                        <UsersIconBlue/>
                    }
                    value={dashboardData.totalEmployees}
                    subtitle="Aktif personeller"
                    iconVariant="users"
                />

                <DashboardCard
                    title="Günlük Yapılan İşlem"
                    icon={
                        <UserActivityIcon/>
                    }
                    value={dashboardData.dailyTransactions}
                    subtitle="Son 24 saat"
                    iconVariant="trend-up"
                />
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