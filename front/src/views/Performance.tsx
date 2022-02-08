import {Sidebar} from "../components/Sidebar";
import React from "react";
import {Container, Row} from "react-bootstrap";

export const Performance = () => {
    return (
        <Container fluid>
            <Row>
                <Sidebar selected="perf" />
                <div id="content-wrapper" >
                    Perf page
                </div>
            </Row>
        </Container>
    )
}