import api from './api';


export const createIndividualCustomer = async (customerData) => {
    try {
        console.log('Creating customer with data:', customerData);
        console.log('Customer API endpoint: /customers/individual (base: /api/v1)');
        const response = await api.post('/customers/individual', customerData);
        console.log('Customer API response:', response.data);
        return response.data;
    } catch (error) {
        console.error('Individual customer creation error:', error);
        console.error('Error details:', error.response?.data);
        throw error;
    }
};


export const createCorporateCustomer = async (customerData) => {
    try {
        const response = await api.post('/customers/corporate', customerData);
        return response.data;
    } catch (error) {
        console.error('Corporate customer creation error:', error);
        throw error;
    }
};

export const getCustomerByEmail = async (email) => {
  try {
    const response = await api.get(`/customers/email/${encodeURIComponent(email)}`);
    return response.data;
  } catch (error) {
    console.error('Get customer by email error:', error);
    throw error;
  }
};

export const getAllCustomers = async () => {
    try {
        const response = await api.get('/customers');
        return response.data;
    } catch (error) {
        console.error('Get all customers error:', error);
        throw error;
    }
};
export const updateIndividualCustomer = async (id, data) => {
    try {
        console.log('Updating individual customer with data:', data);
        const response = await api.put(`/customers/individual/${id}`, data);
        return response.data;
    } catch (error) {
        console.error('Update individual customer error:', error);
        console.error('Error details:', error.response?.data);
        throw error;
    }
};

export const updateCorporateCustomer = async (id, data) => {
    try {
        console.log('Updating corporate customer with data:', data);
        const response = await api.put(`/customers/corporate/${id}`, data);
        return response.data;
    } catch (error) {
        console.error('Update corporate customer error:', error);
        console.error('Error details:', error.response?.data);
        throw error;
    }
};

export const getCustomerById = async (id) => {
    try {
        const response = await api.get(`/customers/${id}`);
        return response.data;
    } catch (error) {
        console.error('Get customer error:', error);
        throw error;
    }
};


export const mapFormDataToCustomerDto = (formData, riskAssessmentResult) => {
    const { firstName, middleName } = parseFullName(formData.firstName);

    return {
        email: formData.email,
        phone: formatPhoneForBackend(formData.phoneNumber),
        nationality: formData.nationality,
        firstName: firstName,
        middleName: middleName,
        lastName: formData.lastName,
        tcNumber: formData.tcNumber,
        birthDate: formData.birthDate,
        birthPlace: formData.birthPlace,
        gender: mapGenderToEnum(formData.gender),
        motherName: formData.motherName || null,
        fatherName: formData.fatherName || null,
        profession: formData.profession || null,
        educationLevel: formData.educationLevel,
        riskProfile: riskAssessmentResult?.riskProfile || null,
        commissionRate: formData.commissionRate ? parseFloat(formData.commissionRate) : null,
        incomeRange: mapIncomeToEnum(formData.monthlyIncome)
    };
};

export const mapCorporateFormDataToCustomerDto = (formData) => {
    return {
        email: formData.email,
        phone: formatPhoneForBackend(formData.phoneNumber),
        nationality: formData.nationality || 'TR',
        companyName: formData.companyName,
        taxNumber: formData.taxNumber,
        tradeRegistryNumber: formData.tradeRegistryNumber || null,
        establishmentDate: formData.establishmentDate || null,
        sector: formData.sector || null,
        authorizedPersonName: formData.authorizedPersonName,
        website: formData.website || null,
        commissionRate: formData.commissionRate ? parseFloat(formData.commissionRate) : null,
        authorizedPersonPhone: formatPhoneForBackend(formData.authorizedPersonPhone),
        authorizedPersonTcNumber: formData.authorizedPersonTcNumber,
        authorizedPersonEmail: formData.authorizedPersonEmail,
        taxOffice: formData.taxOffice
    };
};



const parseFullName = (fullName) => {
    if (!fullName || !fullName.trim()) {
        return { firstName: '', middleName: null };
    }

    const nameParts = fullName.trim().split(/\s+/);

    if (nameParts.length === 1) {
        return { firstName: nameParts[0], middleName: null };
    } else if (nameParts.length === 2) {
        return { firstName: nameParts[0], middleName: nameParts[1] };
    } else {
        return { firstName: nameParts[0], middleName: nameParts.slice(1).join(' ') };
    }
};


const formatPhoneForBackend = (phone) => {
    if (!phone) return null;
    const cleanPhone = phone.replace(/\D/g, '');
    return '+90' + cleanPhone;
};


const mapGenderToEnum = (gender) => {
    const genderMap = {
        'erkek': 'MALE',
        'kadin': 'FEMALE',
        'kadın': 'FEMALE',
        'diger': 'OTHER',
        'diğer': 'OTHER'
    };
    return genderMap[gender?.toLowerCase()] || null;
};


const mapIncomeToEnum = (monthlyIncome) => {
    if (!monthlyIncome) return null;

    const income = parseFloat(monthlyIncome);

    if (income <= 10000) {
        return 'RANGE_0_10K';
    } else if (income <= 25000) {
        return 'RANGE_10K_25K';
    } else if (income <= 50000) {
        return 'RANGE_25K_50K';
    } else if (income <= 100000) {
        return 'RANGE_50K_100K';
    } else {
        return 'RANGE_ABOVE_100K';
    }
};


export const handleCustomerApiError = (error) => {
    if (!error.response) {
	        return 'İnternet bağlantısını kontrol edin';
    }

    const { status, data } = error.response;

    switch (status) {
        case 400:
            if (data.errors) {
                const fieldErrors = Object.values(data.errors);
                return fieldErrors.length > 0 ? fieldErrors[0] : 'Form verilerini kontrol edin';
            }
            return 'Form verilerini kontrol edin';

        case 409:
            if (data.detail) {
                if (data.detail.includes('email')) {
	                    return 'Bu e-posta adresi zaten kullanılıyor';
                }
                if (data.detail.includes('ID') || data.detail.includes('TC')) {
	                    return 'Bu TC Kimlik No zaten kayıtlı';
                }
                if (data.detail.includes('tax number')) {
	                    return 'Bu vergi numarası zaten kayıtlı';
                }
            }
	            return 'Bu bilgiler zaten kayıtlı';

        case 422:
            return data.detail || 'İşlem gerçekleştirilemedi';

        case 500:
	            return 'Sunucu hatası. Lütfen tekrar deneyin';

        default:
            return 'Bir hata oluştu. Lütfen tekrar deneyin';
    }
};