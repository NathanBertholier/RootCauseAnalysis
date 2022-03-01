import {Sidebar} from "../components/Sidebar";
import React from "react";
import {Container, Form, Row} from "react-bootstrap";
import {Graph} from "../components/Graph";

export const Report = () => {
    return (
        <Container fluid>
            <Row>
                <Sidebar selected="report" />
                <div id="content-wrapper" >
                    <h1 className="title">Rapport</h1>
                    <Container fluid>
                        <Row className="background-white report-container">
                            <div className="report-filter">
                                <Form.Text className="text-muted">ID cible :</Form.Text>
                                <Form.Control name="target" type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="report-filter">
                                <Form.Text className="text-muted">Delta de recherche :</Form.Text>
                                <Form.Control name="delta" type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="report-filter">
                                <Form.Text className="text-muted">Taille du réseau :</Form.Text>
                                <Form.Control name="networkSize" type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="report-filter">
                                <Form.Text className="text-muted">Seuil de proximité :</Form.Text>
                                <Form.Control name="proximity" type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div>
                                <Graph />
                            </div>
                        </Row>
                    </Container>
                </div>
            </Row>
        </Container>
    )
}