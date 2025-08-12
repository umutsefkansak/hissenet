import React from 'react';
import styles from './SearchBar.module.css';

const SearchBar = ({ value, onChange, placeholder }) => (
  <input
    type="text"
    className={styles.searchInput}
    value={value}
    onChange={e => onChange(e.target.value)}
    placeholder={placeholder}
  />
);

export default SearchBar;
