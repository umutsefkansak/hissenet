import React from 'react';
import FormInput from '../../Form/FormInput';
import FormSelect from '../../Form/FormSelect';
import { sectorOptions } from '../../../constants/CreateCustomer/formOptions';
import styles from '../../../pages/CreateCustomer/CreateCustomer.module.css';

const CompanyInfoSection = ({ formData, handleInputChange, errors }) => {
    return (
        <div className={styles.formSection}>
            <h3 className={styles.sectionTitle}>ŞİRKET BİLGİLERİ</h3>

            <div className={styles.formRow}>
                <FormInput
                    label="Ticaret Unvanı"
                    name="companyName"
                    value={formData.companyName}
                    onChange={handleInputChange}
                    error={errors.companyName}
                    required
                    placeholder="Şirket ticaret unvanı"
                />

                <FormInput
                    label="Vergi Numarası"
                    name="taxNumber"
                    value={formData.taxNumber}
                    onChange={handleInputChange}
                    error={errors.taxNumber}
                    required
                    placeholder="10 haneli vergi numarası"
                    maxLength="10"
                />
            </div>

            <div className={styles.formRow}>
                <FormInput
                    label="Vergi Dairesi"
                    name="taxOffice"
                    value={formData.taxOffice}
                    onChange={handleInputChange}
                    error={errors.taxOffice}
                    required
                    placeholder="Bağlı bulunduğu vergi dairesi"
                />

                <FormInput
                    label="Ticaret Sicil Numarası"
                    name="tradeRegistryNumber"
                    value={formData.tradeRegistryNumber}
                    onChange={handleInputChange}
                    error={errors.tradeRegistryNumber}
                    placeholder="Ticaret sicil numarası"
                />
            </div>

            <div className={styles.formRow}>
                <FormInput
                    label="Kuruluş Tarihi"
                    name="establishmentDate"
                    type="date"
                    value={formData.establishmentDate}
                    onChange={handleInputChange}
                    error={errors.establishmentDate}
                    required
                />

                <FormSelect
                    label="Sektör"
                    name="sector"
                    value={formData.sector}
                    onChange={handleInputChange}
                    error={errors.sector}
                    required
                    options={sectorOptions}
                    placeholder="Sektör seçiniz"
                />
            </div>

            <div className={styles.formRow}>
                <FormInput
                    label="Web Site"
                    name="website"
                    type="url"
                    value={formData.website}
                    onChange={handleInputChange}
                    error={errors.website}
                    placeholder="https://www.example.com"
                />
            </div>
        </div>
    );
};

export default CompanyInfoSection;