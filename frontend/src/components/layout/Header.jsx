import React from 'react';
import './Header.css';

const Header = () => {
    return (
        <header className="header">
            <div className="header-content">
                <div className="header-left">
                    <h1 className="header-title">⚙️ Equipment Risk Predictor</h1>
                    <p className="header-subtitle">AI-Powered Health Monitoring</p>
                </div>
            </div>
        </header>
    );
};

export default Header;
