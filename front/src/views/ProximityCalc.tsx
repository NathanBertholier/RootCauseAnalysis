import {Sidebar} from "../components/Sidebar";
import React, {useEffect, useState} from "react";
import {Button, Container, Form, Row} from "react-bootstrap";
import GenericTable from "../components/GenericTable";
import DataService from "../services/DataService";
import {LinkResponse} from "../types/LinkResponse";

export const ProximityCalc = () => {

    const [ linkList, setLinkList ] = useState( { computations: [], proximity: 0} as LinkResponse );

    useEffect(() => {
        DataService.getLink().then( (response : any) => {
            console.log(response)
            let links : LinkResponse = response.data
            setLinkList( links );
        } )
    }, [] );

    return (
        <Container fluid>
            <Row>
                <Sidebar selected="proximity" />
                <div id="content-wrapper" >
                    <h1 className="title">Calcul de proximité</h1>
                    <Container fluid className="proximity-container">
                        <Row className="proximity-filters-container">
                            <div className="proximity-filter">
                                <Form.Text className="text-muted">ID n°1 :</Form.Text>
                                <Form.Control type="text" className="custom-input" pattern="^[1-9][0-9]?$|^100$" />
                            </div>
                            <div className="proximity-filter">
                                <Form.Text className="text-muted">ID n°2 :</Form.Text>
                                <Form.Control type="text" className="custom-input" pattern="^[1-9][0-9]?$|^100$" />
                            </div>
                            <div className="proximity-filter">
                                <Button className={`custom-filter-btn`} variant="outline-primary" >Valider</Button>
                            </div>
                        </Row>
                        <Row className="proximity-table-container">
                            <GenericTable  items={ linkList.computations } headers={ { token_type:'Type de token', value_log_first:'Token du log n°1', value_log_second: "Token du log n°2", proximity: "Calcul de l'indice de proximité" } } customRenderers={{}} />
                        </Row>
                        <Row className="proximity-score-container">
                            <div>Score de proximité = { linkList.proximity }</div>
                        </Row>
                    </Container>
                </div>
            </Row>
        </Container>
    )
}