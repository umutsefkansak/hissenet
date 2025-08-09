export const validationRules = {
    name: {
        pattern: /^[a-zA-ZçÇğĞıİöÖşŞüÜ\s]+$/,
        message: "Sadece harf karakterler kullanılabilir"
    },

    email: {
        pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        message: "Geçerli bir e-posta adresi giriniz"
    },

    phone: {
        pattern: /^[0-9]{10}$/,
        message: "10 haneli telefon numarası giriniz"
    },

    position: {
        pattern: /^[a-zA-ZçÇğĞıİöÖşŞüÜ\s\-.,]+$/,
        message: "Pozisyon alanında sadece harf, boşluk ve temel noktalama işaretleri kullanılabilir"
    },

    password: {
        pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,}$/,
        message: "Şifre en az 8 karakter olmalı, büyük harf, küçük harf ve rakam içermelidir"
    }
};

export const cleanInput = (value, type) => {
    if (!value) return value;

    switch (type) {
        case 'name':
            return value.replace(/[^a-zA-ZçÇğĞıİöÖşŞüÜ\s]/g, '');

        case 'phone':
            return value.replace(/[^0-9]/g, '').slice(0, 10);

        case 'email':
            return value.trim().toLowerCase();

        default:
            return value.trim();
    }
};

export const fieldTypes = {
    firstName: 'name',
    lastName: 'name',
    phone: 'phone',
    emergencyContactName: 'name',
    emergencyContactPhone: 'phone',
    email: 'email'
};