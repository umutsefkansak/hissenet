import React from 'react';
import FormInput from '../../Form/FormInput';
import styles from '../../../pages/CreateCustomer/CreateCustomer.module.css';

const AuthorizedPersonSection = ({ formData, handleInputChange, errors }) => {
    return (
        <div className={styles.formSection}>
            <h3 className={styles.sectionTitle}>YETKİLİ KİŞİ BİLGİLERİ</h3>

            <div className={styles.formRow}>
                <FormInput
                    label="Adı Soyadı"
                    name="authorizedPersonName"
                    value={formData.authorizedPersonName}
                    onChange={handleInputChange}
                    error={errors.authorizedPersonName}
                    required
                    placeholder="Yetkili kişinin adı soyadı"
                />

                <FormInput
                    label="TC Kimlik Numarası"
                    name="authorizedPersonTcNumber"
                    value={formData.authorizedPersonTcNumber}
                    onChange={handleInputChange}
                    error={errors.authorizedPersonTcNumber}
                    required
                    placeholder="11 haneli TC kimlik numarası"
                    maxLength="11"
                />
            </div>

            <div className={styles.formRow}>
                <div className={styles.formComponentGroup}>
                    <label htmlFor="authorizedPersonPhone">
                        Cep Telefonu <span className={styles.required}>*</span>
                    </label>
                    <div className={`${styles.phoneInputContainer} ${errors.authorizedPersonPhone ? styles.error : ''}`}>
                        <span className={styles.phonePrefix}>+90</span>
                        <input
                            type="tel"
                            id="authorizedPersonPhone"
                            name="authorizedPersonPhone"
                            value={formData.authorizedPersonPhone}
                            onChange={handleInputChange}
                            className={`${errors.authorizedPersonPhone ? styles.error : ''} ${styles.phoneInput}`}
                            placeholder="5XX XXX XX XX"
                            maxLength="10"
                        />
                    </div>
                    {errors.authorizedPersonPhone && <span className={styles.errorText}>{errors.authorizedPersonPhone}</span>}
                </div>

                <FormInput
                    label="E-posta Adresi"
                    name="authorizedPersonEmail"
                    type="email"
                    value={formData.authorizedPersonEmail}
                    onChange={handleInputChange}
                    error={errors.authorizedPersonEmail}
                    required
                    placeholder="yetkili@sirket.com"
                />
            </div>
        </div>
    );
};

export default AuthorizedPersonSection;