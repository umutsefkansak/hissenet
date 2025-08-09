import React from 'react';

const SortableHeader = ({ columnKey, label, sortConfig, onSort, className = '' }) => {
  const active = sortConfig.key === columnKey;
  const dir = active ? sortConfig.direction : null;
  return (
    <th
      className={`sortable-header ${className}`}
      onClick={() => onSort(columnKey)}
      role="button"
    >
      <span>{label}</span>
      <span className={`sort-indicator ${dir === 'asc' ? 'asc' : dir === 'desc' ? 'desc' : ''}`}>
        {active ? (dir === 'asc' ? '↑' : '↓') : '↕'}
      </span>
    </th>
  );
};

export default SortableHeader;