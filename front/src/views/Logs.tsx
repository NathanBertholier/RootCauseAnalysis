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
                    <h1 className="title" >Logs</h1>
                    <LogList />
                </div>
            </Row>
        </Container>
    )
}