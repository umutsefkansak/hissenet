import { useState, useEffect } from 'react';
import { getCities, getDistrictsByCityCode } from 'turkey-neighbourhoods';

export const useLocation = () => {
    const [cities, setCities] = useState([]);
    const [districts, setDistricts] = useState([]);
    const [isLoadingDistricts, setIsLoadingDistricts] = useState(false);

    useEffect(() => {
        const cityOptions = getCities().map(city => ({
            value: city.name, 
            label: city.name,
            code: city.code   
        }));
        setCities(cityOptions);
    }, []);

    const loadDistricts = (cityName) => {
        if (!cityName) {
            setDistricts([]);
            return;
        }

        setIsLoadingDistricts(true);
        try {
            const selectedCity = getCities().find(city => city.name === cityName);
            if (!selectedCity) {
                console.error('Şehir bulunamadı:', cityName);
                setDistricts([]);
                return;
            }

            const districtOptions = getDistrictsByCityCode(selectedCity.code).map(district => ({
                value: district,
                label: district
            }));
            setDistricts(districtOptions);
        } catch (error) {
            console.error('İlçe yükleme hatası:', error);
            setDistricts([]);
        } finally {
            setIsLoadingDistricts(false);
        }
    };

    return { cities, districts, isLoadingDistricts, loadDistricts };
};