import React from "react";
import {Sidebar} from "../components/Sidebar";
import {Container, Row} from "react-bootstrap";

export const Home = () => {
    return (
        <Container fluid>
            <Row>
                <Sidebar />
                <div id="content-wrapper" >
                    Home page
                </div>
            </Row>
        </Container>
    )
}