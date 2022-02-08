import {Sidebar} from "../components/Sidebar";
import React from "react";
import {Container, Row} from "react-bootstrap";

export const Logs = () => {
    return (
        <Container fluid>
            <Row>
                <Sidebar selected="logs" />
                <div id="content-wrapper" >
                    Logs page
                </div>
            </Row>
        </Container>
    )
}