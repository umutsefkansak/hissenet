import React from 'react';
import FormSelect from './FormSelect';
import { useLocation } from '../../hooks/CreateCustomer/useLocation';

const LocationSelector = ({
                              cityValue,
                              districtValue,
                              onCityChange,
                              onDistrictChange,
                              errors
                          }) => {
    const { cities, districts, isLoadingDistricts, loadDistricts } = useLocation();

    const handleCityChange = (e) => {
        const cityCode = e.target.value;
        onCityChange(e);

        // İlçe seçimini sıfırla
        onDistrictChange({
            target: { name: 'district', value: '' }
        });

        // Yeni ilçeleri yükle
        loadDistricts(cityCode);
    };

    return (
        <div className="form-row">
            <FormSelect
                label="İl"
                name="city"
                value={cityValue}
                onChange={handleCityChange}
                error={errors.city}
                required
                options={cities}
                placeholder="İl seçiniz"
            />

            <FormSelect
                label="İlçe"
                name="district"
                value={districtValue}
                onChange={onDistrictChange}
                error={errors.district}
                options={districts}
                placeholder={
                    isLoadingDistricts
                        ? "İlçeler yükleniyor..."
                        : cityValue
                            ? "İlçe seçiniz"
                            : "Önce il seçiniz"
                }
                disabled={!cityValue || isLoadingDistricts}
            />
        </div>
    );
};

export default LocationSelector;