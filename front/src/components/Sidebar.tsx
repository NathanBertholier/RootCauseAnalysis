import React from "react";
import { Link } from 'react-router-dom';
import perfLogo from '../res/images/gray/stat.svg';
import reportLogo from '../res/images/gray/network.svg';
import calcLogo from '../res/images/gray/calc.svg';
import logsLogo from '../res/images/gray/logs.svg';

import {MenuItem} from "./MenuItem";

type Menu = {
    selected: 'home' | 'logs' | 'perf' | 'proximity' | 'report'
}

export  const Sidebar = (prop: Menu) => {
    return (
        <div className="sidebar">
            <Link to="/" className="projectTitle">
                <h1>Root Cause Analysis</h1>
            </Link>

            <MenuItem link="/perf" logo={perfLogo} text="Performance" isSelected={ prop.selected === "perf" } />
            <MenuItem link="/logs" logo={logsLogo} text="Logs" isSelected={prop.selected === "logs"} />
            <MenuItem link="/proximity" logo={calcLogo} text="Calcul de proximité" isSelected={prop.selected === "proximity"} />
            <MenuItem link="/report" logo={reportLogo} text="Rapport" isSelected={prop.selected === "report"} />
        </div>
    )
}