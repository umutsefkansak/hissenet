import React from 'react';
import FormInput from '../../Form/FormInput';
import FormSelect from '../../Form/FormSelect';
import LocationSelector from "../../Form/LocationSelector";
import styles from '../../../pages/CreateCustomer/CreateCustomer.module.css';

const CorporateContactInfoSection = ({ formData, handleInputChange, errors }) => {
    return (
        <div className={styles.formSection}>
            <h3 className={styles.sectionTitle}>İLETİŞİM BİLGİLERİ</h3>

            <div className={styles.formRow}>
                <div className={styles.formComponentGroup}>
                    <label htmlFor="phoneNumber">
                        Sabit Tel <span className={styles.required}>*</span>
                    </label>
                    <div className={`${styles.phoneInputContainer} ${errors.phoneNumber ? styles.error : ''}`}>
                        <span className={styles.phonePrefix}>+90</span>
                        <input
                            type="tel"
                            id="phoneNumber"
                            name="phoneNumber"
                            value={formData.phoneNumber}
                            onChange={handleInputChange}
                            className={`${errors.phoneNumber ? styles.error : ''} ${styles.phoneInput}`}
                            placeholder="2XX XXX XX XX"
                            maxLength="10"
                        />
                    </div>
                    {errors.phoneNumber && <span className={styles.errorText}>{errors.phoneNumber}</span>}
                </div>

                <FormInput
                    label="E-posta"
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    error={errors.email}
                    required
                    placeholder="info@sirket.com"
                />
            </div>

            <div className={styles.formRow}>
                <FormSelect
                    label="Adres Tipi"
                    name="addressType"
                    value={formData.addressType}
                    onChange={handleInputChange}
                    error={errors.addressType}
                    required
                    options={[
                        { value: 'WORK', label: 'İş Adresi' },
                        { value: 'CORRESPONDENCE', label: 'Yazışma Adresi' },
                        { value: 'OTHER', label: 'Diğer' }
                    ]}
                    placeholder="Adres tipi seçiniz"
                />

                <FormInput
                    label="Posta Kodu"
                    name="postalCode"
                    value={formData.postalCode}
                    onChange={handleInputChange}
                    error={errors.postalCode}
                    placeholder="34000"
                    maxLength="5"
                />
            </div>

            <div className={`${styles.formRow} ${styles.fullWidth}`}>
                <FormInput
                    label="Sokak/Cadde/Mahalle"
                    name="street"
                    value={formData.street}
                    onChange={handleInputChange}
                    error={errors.street}
                    required
                    placeholder="Sokak/Cadde/Mahalle bilgisi"
                />
            </div>

            <LocationSelector
                cityValue={formData.city}
                districtValue={formData.district}
                onCityChange={handleInputChange}
                onDistrictChange={handleInputChange}
                errors={errors}
            />


            <div className={styles.formRow}>
                <FormInput
                    label="Komisyon Oranı (%)"
                    name="commissionRate"
                    type="number"
                    value={formData.commissionRate}
                    onChange={handleInputChange}
                    error={errors.commissionRate}
                    placeholder="Komisyon oranı giriniz"
                    step="0.01"
                    min="0"
                    max="100"
                />
            </div>
        </div>
    );
};

export default CorporateContactInfoSection;