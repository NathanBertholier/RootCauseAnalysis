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
                    <Route path="/perf"component={() => {
                        window.location.href = 'http://vps.rootcause.fr:3001/d/nMC1qBank/rate?orgId=1&refresh=5s';
                        return null;
                    }}/>
                    <Route path="/logs">
                        <Logs />
                    </Route>
                    <Route path="/proximity">
                        <ProximityCalc />
                    </Route>
                    <Route path="/report">
                        <Report />
                    </Route>
                </Switch>
            </div>
        </Router>
    );
}

export default App;
