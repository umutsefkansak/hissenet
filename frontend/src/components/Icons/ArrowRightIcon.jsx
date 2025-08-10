import React from "react";

export default function ArrowRightIcon({ className, ...props }) {
    return (
        <svg
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            className={className}
            {...props}
        >
            <path d="M9 5l7 7-7 7" />
        </svg>
    );
}