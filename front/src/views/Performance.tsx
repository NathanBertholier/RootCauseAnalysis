import {Sidebar} from "../components/Sidebar";
import React from "react";
import {Container, Row} from "react-bootstrap";

export const Performance = () => {
    return (
        <Container fluid>
            <Row>
                <Sidebar selected="perf" />
                <div id="content-wrapper" >
                    <h1 className="title" >Performance</h1>
                    <Container fluid>
                        <Row className="graph-container" >
                            <h2 className="graph-title">Volumes</h2>
                            <iframe src="http://localhost:3001/d-solo/nMC1qBank/rate?orgId=1&refresh=5s&&panelId=6"
                                width="450" height="200" frameBorder="0" title="panel1" />
                        </Row>
                        <Row className="graph-container" >
                            <h2 className="graph-title">Débit</h2>
                            <iframe src="http://localhost:3001/d-solo/nMC1qBank/rate?orgId=1&refresh=5s&&panelId=4"
                                width="450" height="200" frameBorder="0" title="panel2" />
                        </Row>
                        <Row className="graph-container">
                            <h2 className="graph-title">Graphique</h2>
                            <iframe src="http://localhost:3001/d-solo/nMC1qBank/rate?orgId=1&refresh=5s&from=1645088758130&to=1645089058130&panelId=2"
                                width="450" height="200" frameBorder="0" title="panel3" />
                        </Row>
                    </Container>
                </div>
            </Row>
        </Container>
    )
}