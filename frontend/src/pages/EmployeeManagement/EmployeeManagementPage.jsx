import React, { useEffect,useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useEmployees from '../../hooks/EmployeeManagement/useEmployees';
import EmployeeTable from '../../components/EmployeeManagement/EmployeeTable';
import EmployeeForm from '../../components/EmployeeManagement/EmployeeForm';
import EmployeeModal from '../../components/EmployeeManagement/EmployeeModal';
import SearchInput from '../../components/common/SearchInput/SearchInput';
import './EmployeeManagementPage.css';
import Pagination from "../../components/common/Pagination/Pagination";
import DashboardCard from '../../components/common/Card/DashboardCard';
import { orderApi } from '../../server/order';
import TrendArrow from '../../components/Icons/TrendArrow';
import UsersIconBlue from '../../components/Icons/UsersIconBlue';
import UserActivityIcon from '../../components/Icons/UserActivityIcon';
import PlusIcon from '../../components/Icons/PlusIcon';
import { isAdmin } from '../../utils/authUtils';


const EmployeeManagementPage = () => {
    const navigate = useNavigate();
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

    // ADMIN rolü kontrolü
    useEffect(() => {
        // Önce giriş yapmış mı kontrol et
        const isLoggedIn = localStorage.getItem('isLogin') === 'true';
        
        if (!isLoggedIn) {
            window.showToast && window.showToast(
                'Bu sayfaya erişmek için önce giriş yapmanız gerekmektedir.', 
                'warning', 
                5000
            );
            navigate('/login');
            return;
        }
        
        // Giriş yapmış ama ADMIN değil
        if (!isAdmin()) {
            window.showToast && window.showToast(
                'Yetkiniz yok! Bu sayfaya erişim için ADMIN rolü gereklidir.', 
                'error', 
                5000
            );
            // Navigate'i kaldırıyoruz, kullanıcı sayfada kalacak ama error page görecek
            return;
        }
    }, [navigate]);

    // Dashboard verilerini yükle
    useEffect(() => {
        if (!isAdmin()) return; // ADMIN değilse veri yükleme

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

    // Eğer giriş yapmamışsa veya ADMIN değilse, sayfa içeriğini gösterme
    const isLoggedIn = localStorage.getItem('isLogin') === 'true';
    
    if (!isLoggedIn) {
        return (
            <div className="employee-management-page">
                <div className="error-page-container">
                    <div className="error-content">
                        <div className="error-icon">
                            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <circle cx="12" cy="12" r="10" stroke="#fbbf24" strokeWidth="2"/>
                                <path d="M12 6v6" stroke="#fbbf24" strokeWidth="2" strokeLinecap="round"/>
                                <path d="M12 16h.01" stroke="#fbbf24" strokeWidth="2" strokeLinecap="round"/>
                            </svg>
                        </div>
                        <h1 className="error-code warning">401</h1>
                        <h2 className="error-title">Giriş Gerekli</h2>
                        <p className="error-message">
                            Bu sayfaya erişmek için önce giriş yapmanız gerekmektedir.
                        </p>
                        <p className="error-description warning">
                            Lütfen geçerli kullanıcı bilgileriniz ile sisteme giriş yapın.
                        </p>
                        <div className="error-actions">
                            <button onClick={() => navigate('/login')} className="primary-btn">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M15 3h6v18h-6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                    <path d="M10 17l5-5-5-5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                    <path d="M15 12H3" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                </svg>
                                Giriş Yap
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
    
    if (!isAdmin()) {
        return (
            <div className="employee-management-page">
                <div className="error-page-container">
                    <div className="error-content">
                        <div className="error-icon forbidden">
                            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <circle cx="12" cy="12" r="10" stroke="#ef4444" strokeWidth="2"/>
                                <path d="M4.93 4.93l14.14 14.14" stroke="#ef4444" strokeWidth="2" strokeLinecap="round"/>
                            </svg>
                        </div>
                        <h1 className="error-code">403</h1>
                        <h2 className="error-title">Erişim Yasak</h2>
                        <p className="error-message">
                            Bu sayfaya erişim yetkiniz bulunmamaktadır.
                        </p>
                        <p className="error-description">
                            Personel Yönetimi sayfasına erişmek için <strong>ADMIN</strong> rolüne sahip olmanız gerekmektedir.
                        </p>
                        <div className="error-actions">
                            <button onClick={() => navigate('/')} className="primary-btn">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                    <polyline points="9,22 9,12 15,12 15,22" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                </svg>
                                Ana Sayfaya Dön
                            </button>
                            <button onClick={() => navigate(-1)} className="secondary-btn">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M19 12H5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                    <path d="M12 19l-7-7 7-7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                                </svg>
                                Geri Dön
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }


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