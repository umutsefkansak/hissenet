import React, { useState, useEffect, useCallback, useRef } from 'react';
import './SearchInput.css';

const SearchInput = ({
                         placeholder = "Ara...",
                         onSearch,
                         onClear,
                         debounceMs = 300,
                         showClearButton = true,
                         showSearchIcon = true,
                         disabled = false,
                         className = "",
                         value = "",
                         onChange,
                         multiField = false,
                         searchFields = [],
                         filterOptions = [],
                         onFilterChange,
                         showFilterDropdown = false,
                         size = "medium",
                         variant = "default"
                     }) => {
    const [searchTerm, setSearchTerm] = useState(value);
    const [selectedFilter, setSelectedFilter] = useState('all');
    const [isFocused, setIsFocused] = useState(false);

    const debounceTimer = useRef(null);

    const isFirstMount = useRef(true);

    const debouncedSearch = useCallback((searchValue, filterValue) => {
        if (debounceTimer.current) {
            clearTimeout(debounceTimer.current);
        }

        debounceTimer.current = setTimeout(() => {
            onSearch?.(searchValue, filterValue);
        }, debounceMs);
    }, [onSearch, debounceMs]);

    useEffect(() => {
        if (value !== searchTerm) {
            setSearchTerm(value);
        }
    }, [value]);

    useEffect(() => {
        if (isFirstMount.current) {
            isFirstMount.current = false;
            return;
        }

        if (debounceMs > 0) {
            debouncedSearch(searchTerm, selectedFilter);
        }

        return () => {
            if (debounceTimer.current) {
                clearTimeout(debounceTimer.current);
            }
        };
    }, [searchTerm, selectedFilter, debouncedSearch, debounceMs]);

    const handleInputChange = (e) => {
        const newValue = e.target.value;
        setSearchTerm(newValue);
        onChange?.(newValue);
    };

    const handleClear = () => {
        setSearchTerm('');
        onChange?.('');
        onClear?.();
        onSearch?.('', selectedFilter);

        if (debounceTimer.current) {
            clearTimeout(debounceTimer.current);
        }
    };

    const handleFilterChange = (e) => {
        const newFilter = e.target.value;
        setSelectedFilter(newFilter);
        onFilterChange?.(newFilter);
        onSearch?.(searchTerm, newFilter);

        if (debounceTimer.current) {
            clearTimeout(debounceTimer.current);
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            if (debounceTimer.current) {
                clearTimeout(debounceTimer.current);
            }
            onSearch?.(searchTerm, selectedFilter);
        }
        if (e.key === 'Escape') {
            handleClear();
        }
    };

    useEffect(() => {
        return () => {
            if (debounceTimer.current) {
                clearTimeout(debounceTimer.current);
            }
        };
    }, []);

    const getPlaceholderText = () => {
        if (multiField && searchFields.length > 0) {
            return `${searchFields.join(', ')} içinde ara...`;
        }
        return placeholder;
    };

    const containerClasses = [
        'search-input-container',
        `search-input-${size}`,
        `search-input-${variant}`,
        isFocused ? 'focused' : '',
        disabled ? 'disabled' : '',
        className
    ].filter(Boolean).join(' ');

    return (
        <div className={containerClasses}>
            {showFilterDropdown && filterOptions.length > 0 && (
                <div className="search-filter">
                    <select
                        value={selectedFilter}
                        onChange={handleFilterChange}
                        className="filter-select"
                        disabled={disabled}
                    >
                        <option value="all">Tümü</option>
                        {filterOptions.map(option => (
                            <option key={option.value} value={option.value}>
                                {option.label}
                            </option>
                        ))}
                    </select>
                </div>
            )}

            <div className="search-input-wrapper">
                {showSearchIcon && (
                    <div className="search-icon">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <circle cx="11" cy="11" r="8" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <path d="21 21L16.65 16.65" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                    </div>
                )}

                <input
                    type="text"
                    className="search-input"
                    placeholder={getPlaceholderText()}
                    value={searchTerm}
                    onChange={handleInputChange}
                    onKeyDown={handleKeyDown}
                    onFocus={() => setIsFocused(true)}
                    onBlur={() => setIsFocused(false)}
                    disabled={disabled}
                />

                {showClearButton && searchTerm && (
                    <button
                        type="button"
                        className="clear-button"
                        onClick={handleClear}
                        disabled={disabled}
                        aria-label="Aramayı temizle"
                    >
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                            <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        </svg>
                    </button>
                )}
            </div>
        </div>
    );
};

export default SearchInput;