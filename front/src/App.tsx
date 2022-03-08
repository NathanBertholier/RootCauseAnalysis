import React from 'react';
import './App.css';
import './tools/toast.css';
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
                        <Performance />
                    </Route>
                    <Route path="/perf">
                        <Performance />
                    </Route>
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
