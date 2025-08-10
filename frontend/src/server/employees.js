import api from './api';

export const getAllEmployees = async () => {
  try {
    const response = await api.get('/employees');
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Get employees error:', error);
    return { success: false, error: error.response?.data || 'Failed to get employees' };
  }
};

// Change password
export const changePassword = async (email, password, confirmNewPassword) => {
  try {
    const response = await api.patch('/employees/changePassword', {
      email,
      password,
      confirmNewPassword
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Change password error:', error);
    // Error response'u daha iyi handle et
    const errorData = error.response?.data;
    const errorMessage = errorData?.message || errorData?.detail || 'Şifre değiştirilemedi';
    return { success: false, error: errorMessage };
  }
};

export const createEmployee = async (employeeData) => {
  try {
    const createdByEmployeeId = localStorage.getItem('personnelId') ?
        parseInt(localStorage.getItem('personnelId')) : null;

    const dataWithCreator = {
      ...employeeData,
      createdByEmployeeId: createdByEmployeeId
    };

    const response = await api.post('/employees', dataWithCreator);
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Create employee error:', error);
    console.error('Error response:', error.response?.data);
    console.error('Error status:', error.response?.status);

    const errorMessage = handleEmployeeApiError(error);
    return { success: false, error: errorMessage };
  }
};

export const getAllEmployeesPageable = async (params = {}) => {
  try {
    const searchParams = new URLSearchParams({
      page: params.page || 0,
      size: params.size || 10,
      sortBy: params.sortBy || 'id',
      sortDir: params.sortDir || 'asc'
    });

    const response = await api.get(`/employees/pageable?${searchParams}`);
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Get employees pageable error:', error);
    return { success: false, error: error.response?.data || 'Failed to get employees' };
  }
};


export const updateEmployee = async (employeeData) => {
  try {
    const updatedByEmployeeId = localStorage.getItem('personnelId') ?
        parseInt(localStorage.getItem('personnelId')) : null;

    const dataWithUpdater = {
      ...employeeData,
      updatedByEmployeeId: updatedByEmployeeId
    };
    const response = await api.put('/employees', dataWithUpdater);

    return { success: true, data: response.data };
  } catch (error) {
    console.error('Update employee error:', error);
    console.error('Error response:', error.response?.data);
    console.error('Error status:', error.response?.status);
    const errorMessage = handleEmployeeApiError(error);
    return { success: false, error: errorMessage };
  }
};

export const getEmployee = async (id) => {
  try {
    const response = await api.get(`/employees/${id}`);
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Get employee error:', error);
    return { success: false, error: error.response?.data || 'Failed to get employee' };
  }
};

export const deleteEmployee = async (employee) => {
  try {
    const id = typeof employee === 'object' ? employee.id : employee;
    const response = await api.delete(`/employees/${id}`);
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Delete employee error:', error);
    return { success: false, error: error.response?.data || 'Failed to delete employee' };
  }
};

const handleEmployeeApiError = (error) => {
  if (!error.response) {
    return 'İnternet bağlantısını kontrol edin';
  }

  const { status, data } = error.response;

  switch (status) {
    case 400:
      if (data.errors && typeof data.errors === 'object') {
        const errorMessages = Object.entries(data.errors).map(([field, message]) => {
          return translateErrorMessage(message);
        });
        return errorMessages.join(', ');
      }
      if (data.detail) {
        return translateErrorMessage(data.detail);
      }
      if (data.message) {
        return translateErrorMessage(data.message);
      }
      return 'Form verilerini kontrol edin';

    case 409:
      if (data.detail) {
        if (data.detail.toLowerCase().includes('email')) {
          return 'Bu e-posta adresi zaten kullanılıyor';
        }
        return translateErrorMessage(data.detail);
      }
      if (data.message) {
        if (data.message.toLowerCase().includes('email')) {
          return 'Bu e-posta adresi zaten kullanılıyor';
        }
        return translateErrorMessage(data.message);
      }
      if (typeof data === 'string') {
        return translateErrorMessage(data);
      }
      return 'Bu bilgiler zaten kayıtlı';

    case 422:
      if (data.detail) {
        return translateErrorMessage(data.detail);
      }
      if (data.message) {
        return translateErrorMessage(data.message);
      }
      return 'İşlem gerçekleştirilemedi';

    case 500:
      return 'Sunucu hatası. Lütfen tekrar deneyin';

    default:
      if (data.detail) {
        return translateErrorMessage(data.detail);
      }
      if (data.message) {
        return translateErrorMessage(data.message);
      }
      if (typeof data === 'string') {
        return translateErrorMessage(data);
      }
      return 'Bir hata oluştu. Lütfen tekrar deneyin';
  }
};

const translateErrorMessage = (error) => {
  if (typeof error === 'string') {
    if (error === 'This email address is already registered in the system') {
      return 'Bu e-posta adresi zaten kullanılıyor';
    }

    if (error.toLowerCase().includes('email') &&
        (error.toLowerCase().includes('already') || error.toLowerCase().includes('duplicate') ||
            error.toLowerCase().includes('registered') || error.toLowerCase().includes('exists'))) {
      return 'Bu e-posta adresi zaten kullanılıyor';
    }

    if (error.toLowerCase().includes('email') && error.toLowerCase().includes('invalid')) {
      return 'Geçersiz e-posta formatı';
    }

    if (error === 'Invalid phone number') return 'Geçersiz telefon numarası';
    if (error === 'Invalid emergency contact phone number') return 'Geçersiz acil durum telefon numarası';
    if (error === 'Password must contain at least one uppercase and one lowercase letter') return 'Şifre en az bir büyük harf ve bir küçük harf içermelidir';
    if (error === 'At least one role ID must be provided') return 'En az bir rol seçmelisiniz';
    if (error === 'First name is required') return 'Ad gereklidir';
    if (error === 'Last name is required') return 'Soyad gereklidir';
    if (error === 'Email is required') return 'E-posta gereklidir';
    if (error === 'Invalid email format') return 'Geçersiz e-posta formatı';
    if (error === 'Phone is required') return 'Telefon gereklidir';
    if (error === 'Position is required') return 'Pozisyon gereklidir';
    if (error === 'Password is required') return 'Şifre gereklidir';

    if (error.toLowerCase().includes('phone') && error.toLowerCase().includes('invalid')) {
      return 'Geçersiz telefon numarası';
    }
    if (error.toLowerCase().includes('password') && error.toLowerCase().includes('contain')) {
      return 'Şifre formatı geçersiz';
    }
    if (error.toLowerCase().includes('role') && error.toLowerCase().includes('provided')) {
      return 'En az bir rol seçmelisiniz';
    }

    return error;
  }

  if (error?.message) {
    if (error.message.includes('email') && (error.message.includes('already exists') || error.message.includes('duplicate'))) {
      return 'Bu e-posta adresi zaten kullanılıyor';
    }
    if (error.message.includes('Employee not found')) {
      return 'Personel bulunamadı';
    }
    if (error.message.includes('Validation failed')) {
      return 'Girilen bilgiler geçersiz, lütfen kontrol edin';
    }
    return error.message;
  }

  if (error?.errors && Array.isArray(error.errors)) {
    const messages = error.errors.map(err => {
      if (err.field === 'email') return 'E-posta adresi geçersiz';
      if (err.field === 'firstName') return 'Ad geçersiz (2-50 karakter)';
      if (err.field === 'lastName') return 'Soyad geçersiz (2-50 karakter)';
      if (err.field === 'phone') return 'Telefon numarası geçersiz';
      if (err.field === 'position') return 'Pozisyon geçersiz';
      return err.defaultMessage || err.message || 'Geçersiz alan';
    });
    return messages.join(', ');
  }

  if (error?.errors && typeof error.errors === 'object') {
    const messages = Object.entries(error.errors).map(([field, message]) => {
      if (field === 'email') return 'E-posta: ' + (Array.isArray(message) ? message[0] : message);
      if (field === 'firstName') return 'Ad: ' + (Array.isArray(message) ? message[0] : message);
      if (field === 'lastName') return 'Soyad: ' + (Array.isArray(message) ? message[0] : message);
      return field + ': ' + (Array.isArray(message) ? message[0] : message);
    });
    return messages.join(', ');
  }

  return error?.detail || error?.title || 'Personel işlemi sırasında bir hata oluştu';
};
