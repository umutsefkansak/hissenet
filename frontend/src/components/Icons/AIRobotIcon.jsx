import React from "react";

export default function AIRobotIcon({ className, ...props }) {
    return (
        <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.5"
            strokeLinecap="round"
            strokeLinejoin="round"
            className={className}
            {...props}
        >
            <rect x="4" y="6" width="16" height="10" rx="3" fill="currentColor" fillOpacity="0.1"/>
            <circle cx="8.5" cy="10" r="1.5" fill="currentColor"/>
            <circle cx="15.5" cy="10" r="1.5" fill="currentColor"/>
            <path d="M10 13h4" strokeWidth="2"/>
            <line x1="8" y1="6" x2="8" y2="3"/>
            <line x1="16" y1="6" x2="16" y2="3"/>
            <circle cx="8" cy="3" r="1" fill="currentColor"/>
            <circle cx="16" cy="3" r="1" fill="currentColor"/>
            <rect x="6" y="16" width="12" height="6" rx="2" fill="currentColor" fillOpacity="0.1"/>
            <rect x="2" y="18" width="4" height="2" rx="1" fill="currentColor" fillOpacity="0.1"/>
            <rect x="18" y="18" width="4" height="2" rx="1" fill="currentColor" fillOpacity="0.1"/>
        </svg>
    );
}