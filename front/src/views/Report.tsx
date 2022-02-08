import {Sidebar} from "../components/Sidebar";
import React from "react";
import {Container, Row} from "react-bootstrap";

export const Report = () => {
    return (
        <Container fluid>
            <Row>
                <Sidebar />
                <div id="content-wrapper" >
                    report page
                </div>
            </Row>
        </Container>
    )
}