import React from 'react';
import FormInput from '../../Form/FormInput';
import FormSelect from '../../Form/FormSelect';
import LocationSelector from '../../Form/LocationSelector';


const ContactInfoSection = ({ formData, handleInputChange, errors }) => {
    return (
        <div className="form-section">
            <h3 className="section-title">İLETİŞİM BİLGİLERİ</h3>

            <div className="form-row">
                <div className="form-group">
                    <label htmlFor="phoneNumber">
                        Cep Telefonu <span className="required">*</span>
                    </label>
                    <div className={`phone-input-container ${errors.phoneNumber ? 'error' : ''}`}>
                        <span className="phone-prefix">+90</span>
                        <input
                            type="tel"
                            id="phoneNumber"
                            name="phoneNumber"
                            value={formData.phoneNumber}
                            onChange={handleInputChange}
                            className={errors.phoneNumber ? 'error phone-input' : 'phone-input'}
                            placeholder="5XX XXX XX XX"
                            maxLength="10"
                        />
                    </div>
                </div>

                <FormInput
                    label="E-posta"
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    error={errors.email}
                    required
                    placeholder="E-posta"
                />
            </div>

            <div className="form-row">
                <FormSelect
                    label="Adres Tipi"
                    name="addressType"
                    value={formData.addressType}
                    onChange={handleInputChange}
                    error={errors.addressType}
                    required
                    options={[
                        { value: 'HOME', label: 'Ev Adresi' },
                        { value: 'WORK', label: 'İş Adresi' },
                        { value: 'CORRESPONDENCE', label: 'Yazışma Adresi' }
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

            <div className="form-row full-width">
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

            <div className="form-row">
                <FormInput
                    label="Bölge"
                    name="state"
                    value={formData.state}
                    onChange={handleInputChange}
                    error={errors.state}
                    required
                    placeholder="Marmara"
                />

                <FormInput
                    label="Ülke"
                    name="country"
                    value={formData.country}
                    onChange={handleInputChange}
                    error={errors.country}
                    required
                    placeholder="Turkey"
                />
            </div>
        </div>
    );
};

export default ContactInfoSection;