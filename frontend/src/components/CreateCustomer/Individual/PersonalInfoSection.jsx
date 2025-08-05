import React from 'react';
import FormInput from '../../Form/FormInput';
import FormSelect from '../../Form/FormSelect';
import { educationOptions, genderOptions, nationalityOptions } from '../../../constants/CreateCustomer/formOptions';
import { useLocation } from '../../../hooks/CreateCustomer/useLocation';

const PersonalInfoSection = ({ formData, handleInputChange, errors }) => {
    const { cities } = useLocation();


    return (
        <div className="form-section">
            <h3 className="section-title">KİŞİSEL BİLGİLER</h3>

            <div className="form-row">
                <FormInput
                    label="Ad"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleInputChange}
                    error={errors.firstName}
                    required
                    placeholder="Adınız"
                />

                <FormInput
                    label="Soyad"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleInputChange}
                    error={errors.lastName}
                    required
                    placeholder="Soyadınız"
                />
            </div>

            <div className="form-row">
                <FormInput
                    label="Anne Adı"
                    name="motherName"
                    value={formData.motherName}
                    onChange={handleInputChange}
                    placeholder="Anne Adı"
                />

                <FormInput
                    label="Baba Adı"
                    name="fatherName"
                    value={formData.fatherName}
                    onChange={handleInputChange}
                    placeholder="Baba Adı"
                />
            </div>

            <div className="form-row">
                <FormInput
                    label="T.C. Kimlik No"
                    name="tcNumber"
                    value={formData.tcNumber}
                    onChange={handleInputChange}
                    error={errors.tcNumber}
                    required
                    placeholder="T.C. Kimlik No"
                    maxLength="11"
                />

                <FormInput
                    label="Doğum Tarihi"
                    name="birthDate"
                    type="date"
                    value={formData.birthDate}
                    onChange={handleInputChange}
                    error={errors.birthDate}
                    required
                />
            </div>

            <div className="form-row">
                <FormSelect
                    label="Doğum Yeri"
                    name="birthPlace"
                    value={formData.birthPlace}
                    onChange={handleInputChange}
                    error={errors.birthPlace}
                    required
                    options={cities}
                    placeholder="Doğum yeri seçiniz"
                />

                <FormSelect
                    label="Eğitim Düzeyi"
                    name="educationLevel"
                    value={formData.educationLevel}
                    onChange={handleInputChange}
                    error={errors.educationLevel}
                    required
                    options={educationOptions}
                    placeholder="Eğitim düzeyi seçiniz"
                />
            </div>

            <div className="form-row">
                <FormSelect
                    label="Cinsiyet"
                    name="gender"
                    value={formData.gender}
                    onChange={handleInputChange}
                    error={errors.gender}
                    required
                    options={genderOptions}
                    placeholder="Cinsiyet seçiniz"
                />

                <FormSelect
                    label="Uyruk"
                    name="nationality"
                    value={formData.nationality}
                    onChange={handleInputChange}
                    options={nationalityOptions}
                />
            </div>
        </div>
    );
};

export default PersonalInfoSection;