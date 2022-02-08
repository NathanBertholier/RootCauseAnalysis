import {Sidebar} from "../components/Sidebar";
import React from "react";
import {LogList} from "../components/LogList";
import {Container, Row} from "react-bootstrap";

export const Logs = () => {
    return (
        <Container fluid>
            <Row>
                <Sidebar selected="logs" />
                <div id="content-wrapper" >
                    <LogList />
                </div>
            </Row>
        </Container>
    )
}