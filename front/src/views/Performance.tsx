import {Sidebar} from "../components/Sidebar";
import React from "react";
import {Container, Row, Col} from "react-bootstrap";

export const Performance = () => {
    return (
        <Container fluid>
            <Row>
                <Sidebar selected="perf" />
                <div id="content-wrapper" >
                    <h1 className="title">Performance</h1>
                    <Container fluid>
                        <Row className="graph-container" >
                            <Col><iframe src={"https://"+window.location.hostname+":3001/d-solo/nMC1qBank/rootcause?orgId=1&refresh=5s&panelId=6"}
                                    width="100%" height="400" frameBorder="0" title="panel1" /></Col>
                            <Col lg md sm xl xs xxl={8}><iframe src={"https://"+window.location.hostname+":3001/d-solo/nMC1qBank/rootcause?orgId=1&refresh=5s&panelId=2"} width="100%" height="400" frameBorder="0" title="panel2" /></Col>
                            <Col><iframe src={"https://"+window.location.hostname+":3001/d-solo/nMC1qBank/rootcause?orgId=1&refresh=5s&panelId=4"}
                                    width="100%" height="400" frameBorder="0" title="panel3" /></Col>
                        </Row>
                        <Row className="graph-container">
                            <Col lg md sm xl xs xxl={8}>
                                <iframe src={"https://"+window.location.hostname+":3001/d-solo/nMC1qBank/rootcause?orgId=1&refresh=5s&panelId=8"}
                                    width="100%" height="400" frameBorder="0" title="panel4"/>
                            </Col>
                            <Col>
                                <iframe src={"https://"+window.location.hostname+":3001/d-solo/nMC1qBank/rootcause?orgId=1&refresh=5s&panelId=12"}
                                    width="100%" height="400" frameBorder="0" title="panel5"/>
                            </Col>
                        </Row>
                    </Container>
                </div>
            </Row>
        </Container>
    )
}