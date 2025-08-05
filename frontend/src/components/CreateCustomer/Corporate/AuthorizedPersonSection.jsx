import React from 'react';
import FormInput from '../../Form/FormInput';

const AuthorizedPersonSection = ({ formData, handleInputChange, errors }) => {
    return (
        <div className="form-section">
            <h3 className="section-title">YETKİLİ KİŞİ BİLGİLERİ</h3>

            <div className="form-row">
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

            <div className="form-row">
                <div className="form-group">
                    <label htmlFor="authorizedPersonPhone">
                        Cep Telefonu <span className="required">*</span>
                    </label>
                    <div className={`phone-input-container ${errors.authorizedPersonPhone ? 'error' : ''}`}>
                        <span className="phone-prefix">+90</span>
                        <input
                            type="tel"
                            id="authorizedPersonPhone"
                            name="authorizedPersonPhone"
                            value={formData.authorizedPersonPhone}
                            onChange={handleInputChange}
                            className={errors.authorizedPersonPhone ? 'error phone-input' : 'phone-input'}
                            placeholder="5XX XXX XX XX"
                            maxLength="10"
                        />
                    </div>
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