export const validationRules = {
  name: {
    pattern: /^[a-zA-ZçÇğĞıİöÖşŞüÜ\s]+$/,
    message: "Sadece harf karakterler kullanılabilir"
  },
  
  
  tcNumber: {
    pattern: /^[1-9][0-9]{10}$/,
    message: "11 haneli geçerli TC Kimlik No giriniz"
  },
  
  
  taxNumber: {
    pattern: /^[0-9]{10}$/,
    message: "10 haneli vergi numarası giriniz"
  },
  
  
  phone: {
    pattern: /^[0-9]{10}$/,
    message: "10 haneli telefon numarası giriniz"
  },
  
  
  email: {
    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    message: "Geçerli bir e-posta adresi giriniz"
  },
  
  
  number: {
    pattern: /^\d+(\.\d{1,2})?$/,
    message: "Geçerli bir sayı giriniz"
  },
  
  
  postalCode: {
    pattern: /^[0-9]{5}$/,
    message: "5 haneli posta kodu giriniz"
  }
};

export const cleanInput = (value, type) => {
  if (!value) return '';
  
  switch (type) {
    case 'name':
      return value.replace(/[^a-zA-ZçÇğĞıİöÖşŞüÜ\s]/g, '');
    case 'number':
      return value.replace(/[^0-9]/g, '');
    case 'decimal':
      return value.replace(/[^0-9.]/g, '');
    case 'email':
      return value.toLowerCase().trim();
    default:
      return value.trim();
  }
};


export const fieldTypes = {
  firstName: 'name',
  lastName: 'name',
  motherName: 'name',
  fatherName: 'name',
  authorizedPersonName: 'name',
  profession: 'name',
  tcNumber: 'number',
  authorizedPersonTcNumber: 'number',
  taxNumber: 'number',
  phoneNumber: 'number',
  authorizedPersonPhone: 'number',
  postalCode: 'number',
  monthlyIncome: 'number',
  commissionRate: 'decimal',
  email: 'email',
  authorizedPersonEmail: 'email'
};
