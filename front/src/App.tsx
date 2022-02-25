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
                    <Route path="/perf">
                        <Performance />
                    </Route>
                    <Route path="/logs">
                        <Logs />
                    </Route>
                </Switch>
            </div>
        </Router>
    );
}

export default App;
