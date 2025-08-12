export const sortList = (list, key, direction, accessors = {}) => {
 const getVal = accessors[key]
   ? accessors[key]
   : (item) => item[key];

 const toComparable = (v) => {
   if (v === null || v === undefined) return '';
   if (v instanceof Date) return v.getTime();
   if (typeof v === 'number') return v;
   const num = Number(v);
   if (!Number.isNaN(num) && v !== '' && v !== null) return num;
   return String(v).toLowerCase();
 };

 const dirMul = direction === 'asc' ? 1 : -1;

 return [...list].sort((a, b) => {
   let aVal = getVal(a);
   let bVal = getVal(b);

   // Tarih alanları için destek (createdAt gibi)
   if (key.toLowerCase().includes('date') || key === 'createdAt') {
     aVal = new Date(aVal);
     bVal = new Date(bVal);
   }

   aVal = toComparable(aVal);
   bVal = toComparable(bVal);
   if (aVal === bVal) return 0;
   return aVal > bVal ? dirMul : -dirMul;
 });
};