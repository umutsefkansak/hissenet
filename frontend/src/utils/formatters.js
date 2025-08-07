/**
 * Fiyatı “##.## ₺” formatında döner;
 * null/undefined ise “—” gösterir.
 */
export const formatPrice = price =>
    price != null
        ? `${price.toFixed(2)} ₺`
        : '—';

/**
 * Değişim tutarını “+##.##” veya “-##.##” formatında döner;
 * null/undefined ise “0.00” gösterir.
 */
export const formatChange = change =>
    change != null
        ? `${change > 0 ? '+' : ''}${change.toFixed(2)}`
        : '0.00';

/**
 * Değişim oranını “(+##.##%)” veya “(-##.##%)” formatında döner;
 * null/undefined ise “(0.00%)” gösterir.
 */
export const formatRate = rate =>
    rate != null
        ? `${rate > 0 ? '+' : ''}${rate.toFixed(2)}%`
        : '0.00%';

/**
 * Sayının pozitif olup olmadığını kontrol eder.
 */
export const isPositive = value =>
    value != null && value >= 0;

/**
 * Hacim sayısını “1.234.567” gibi Türkçe locale’a göre formatlar;
 * null/undefined ise “-” gösterir.
 */
export function formatHacim(hacim) {
    if (hacim == null) return '-';
    return hacim.toLocaleString('tr-TR');
}