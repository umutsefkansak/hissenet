// Kullanıcının rollerini localStorage'dan al
export const getUserRoles = () => {
  try {
    const roles = localStorage.getItem('userRoles');
    return roles ? JSON.parse(roles) : [];
  } catch (error) {
    console.error('Error parsing user roles:', error);
    return [];
  }
};

// Kullanıcının belirli bir role sahip olup olmadığını kontrol et
export const hasRole = (roleName) => {
  const roles = getUserRoles();
  return roles.includes(roleName);
};

// Kullanıcının ADMIN rolüne sahip olup olmadığını kontrol et
export const isAdmin = () => {
  return hasRole('ADMIN');
};

// Kullanıcının PERSONNEL rolüne sahip olup olmadığını kontrol et
export const isPersonnel = () => {
  return hasRole('PERSONNEL');
};
