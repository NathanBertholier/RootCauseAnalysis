import React from "react";
import { Link } from 'react-router-dom';
import perfLogo from '../res/images/perf.png';
import reportLogo from '../res/images/report.png';
import calcLogo from '../res/images/calc.png';
import logsLogo from '../res/images/logs.png';

export  const Sidebar = () => {
    return (
        <div className="sidebar">
            <Link to="/" className="projectTitle">
                <h1>Root Cause Analysis</h1>
            </Link>

            <Link to="/perf" className="item">
                <div className="item-icon">
                    <img src={perfLogo} alt="logo" />
                </div>
                <div className="item-text">Performance</div>
            </Link>
            <Link to="/logs" className="item">
                <div className="item-icon">
                    <img src={logsLogo} alt="logo" />
                </div>
                <div className="item-text">Logs</div>
            </Link>
            <Link to="/proximity" className="item">
                <div className="item-icon">
                    <img src={calcLogo} alt="logo" />
                </div>
                <div className="item-text">Calcul de proximité</div>
            </Link>
            <Link to="/report" className="item">
                <div className="item-icon">
                    <img src={reportLogo} alt="logo" />
                </div>
                <div className="item-text">Rapport</div>
            </Link>
        </div>
    )
}