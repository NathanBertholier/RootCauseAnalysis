import {Sidebar} from "../components/Sidebar";
import React from "react";
import {Container, Row} from "react-bootstrap";

export const ProximityCalc = () => {
    return (
        <Container fluid>
            <Row>
                <Sidebar selected="proximity" />
                <div id="content-wrapper" >
                    proximity page
                </div>
            </Row>
        </Container>
    )
}