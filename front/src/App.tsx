import React from 'react';
import './App.css';
import './tools/toast.css';
import {Home} from "./views/Home";
import {Performance} from "./views/Performance";
import {Logs} from "./views/Logs";
import {Report} from "./views/Report";
import {ProximityCalc} from "./views/ProximityCalc";
import { HashRouter as Router, Route, Switch } from "react-router-dom";

function App() {
    return (
        <Router>
            <div className="App">
                <Switch>
                    <Route exact path="/">
                        <Home />
                    </Route>
                    <Route path="/perf" component={() => {
                        window.location.href = window.location.protocol+'//'+window.location.hostname+':3001/d/nMC1qBank/rate?orgId=1&refresh=5s';
                        return null;
                    }}/>
                    <Route path="/logs">
                        <Logs />
                    </Route>
                </Switch>
            </div>
        </Router>
    );
}

export default App;
