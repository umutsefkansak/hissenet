// components/Icons/UsersIcon.jsx
import * as React from "react";

export default function UsersIcon({ className = '' }) {
    return (
        <svg
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            className={className}
        >
            <path
                d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
            <circle
                cx="9"
                cy="7"
                r="4"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
            <path
                d="M23 21V19C23 18.1326 22.7035 17.2982 22.1677 16.636C21.6319 15.9738 20.8918 15.5229 20.06 15.36"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
            <path
                d="M16 3.13C16.8318 3.29312 17.5719 3.74398 18.1077 4.40619C18.6435 5.06839 18.94 5.90285 18.94 6.77C18.94 7.63715 18.6435 8.47161 18.1077 9.13381C17.5719 9.79602 16.8318 10.2469 16 10.41"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
        </svg>
    );
}
