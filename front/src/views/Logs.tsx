import {Sidebar} from "../components/Sidebar";
import React from "react";
import {LogList} from "../components/LogList";
import {Container, Row, Form, Button, Col, DropdownButton, Dropdown, FormControl, InputGroup} from "react-bootstrap";
import {toast} from "../tools/ToastManager";

export const Logs = () => {

    return (
        <Container fluid>
            <Row>
                <Sidebar selected="logs" />
                <div id="content-wrapper" >
                    <h1 className="title" >Logs</h1>
                    <Form className="logs-filter" >
                    <Container fluid>
                        <Row>
                            <Col sm={8}>
                                <InputGroup className="mb-3">
                                    <FormControl />

                                    <DropdownButton
                                        variant="outline-secondary"
                                        title="Ajout Filter"
                                        id="custom-log-dropdown"
                                        align="end"
                                    >
                                        <Dropdown.Item >Action</Dropdown.Item>
                                        <Dropdown.Item >Another action</Dropdown.Item>
                                        <Dropdown.Item >Something else here</Dropdown.Item>
                                        <Dropdown.Divider />
                                        <Dropdown.Item href="#">Separated link</Dropdown.Item>
                                    </DropdownButton>
                                </InputGroup>
                            </Col>
                            <Col sm={4}>
                                <Form.Text className="text-muted">Nombre de ligne :</Form.Text>
                                <Form.Control type="number" className="custom-input"  min="1" />
                                <Button className="custom-filter-btn" variant="outline-primary" type="submit" >Valider</Button>
                            </Col>
                        </Row>
                    </Container>
                    </Form>
                    <div onClick={() =>
                        toast.show({
                            content: "Toast content Une erreur s'est produite",
                            duration: 3000,
                        })
                    }>coucou</div>
                    <LogList />
                </div>
            </Row>
        </Container>
    )
}