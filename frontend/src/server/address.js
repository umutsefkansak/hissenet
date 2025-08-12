import api from './api';


export const createAddress = async (addressData) => {
    try {
        console.log('Creating address with data:', addressData);
        console.log('Address API endpoint:', '/addresses (base: /api/v1)');
        const response = await api.post('/addresses', addressData);
        console.log('Address API response:', response.data);
        return response.data;
    } catch (error) {
        console.error('Address creation error:', error);
        console.error('Error details:', error.response?.data);
        throw error;
    }
};


export const getCustomerAddresses = async (customerId) => {
    try {
        const response = await api.get(`/addresses/customer/${customerId}`);
        return response.data;
    } catch (error) {
        console.error('Get customer addresses error:', error);
        throw error;
    }
};


export const getPrimaryAddress = async (customerId) => {
    try {
        const response = await api.get(`/addresses/customer/${customerId}/primary`);
        return response.data;
    } catch (error) {
        console.error('Get primary address error:', error);
        throw error;
    }
};


export const mapFormDataToAddressDto = (formData, customerId) => {
    return {
        addressType: formData.addressType,
        street: formData.street,
        district: formData.district || null,
        city: formData.city,
        state: formData.state,
        country: formData.country,
        postalCode: formData.postalCode || null,
        isPrimary: true,
        customerId: customerId
    };
};


export const handleAddressApiError = (error) => {
    if (!error.response) {
        return 'İnternet bağlantısını kontrol edin';
    }

    const { status, data } = error.response;

    switch (status) {
        case 400:
            if (data.errors) {
                const fieldErrors = Object.values(data.errors);
                return fieldErrors.length > 0 ? fieldErrors[0] : 'Adres verilerini kontrol edin';
            }
            return 'Adres verilerini kontrol edin';

        case 404:
            return 'Müşteri bulunamadı';

        case 500:
            return 'Sunucu hatası. Lütfen tekrar deneyin';

        default:
            return 'Adres kaydında hata oluştu. Lütfen tekrar deneyin';
    }
};