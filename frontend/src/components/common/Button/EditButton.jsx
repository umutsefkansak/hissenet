import React from 'react';
import './EditButton.css';

const EditButton = ({ onClick }) => {
    return (
        <button className="edit-button" onClick={onClick}>
            Düzenle
        </button>
    );
};

export default EditButton;
