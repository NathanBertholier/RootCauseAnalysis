import {Sidebar} from "../components/Sidebar";
import React, {useState} from "react";
import {Button, Container, Form, FormControl, Row} from "react-bootstrap";
import GenericTable from "../components/GenericTable";
import DataService from "../services/DataService";
import {LinkResponse} from "../types/LinkResponse";
import {LinkRequest} from "../types/LinkRequest";
import {toast} from "../tools/ToastManager";
import {FormData, onFocusOut} from "../types/FormData";

const DEFAULT_ID_VALUE : number = 0;
const DEFAULT_DELTA_VALUE : number = 2;

export const ProximityCalc = () => {

    const [ linkList, setLinkList ] = useState( { computations: [], proximity: 0} as LinkResponse );
    const [ formData, setFormData ] = useState({
        "id1":      {value:DEFAULT_ID_VALUE, error:"", isRequired: true},
        "id2":      { value:DEFAULT_ID_VALUE, error: "", isRequired: true },
        "delta":    { value:DEFAULT_DELTA_VALUE, error: "", isRequired: true }
    } as FormData );

    const sendForm = () => {
        if ( formData["id1"].value !== DEFAULT_ID_VALUE && formData["id2"].value !== DEFAULT_ID_VALUE ) {
            let request : LinkRequest = { params: { delta: formData["delta"].value, id1: formData["id1"].value, id2: formData["id2"].value } }
            DataService.getLink( request ).then( (response : any) => {
                console.log(response)
                let links : LinkResponse = response.data
                setLinkList( links );
            } )
        }
        else {
            toast.show({
                title: "Error",
                content: "Un des champs est vide",
                duration: 3000,
            });
        }
    }

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
                                <Form.Control onBlur={ e => onFocusOut(formData, setFormData, e.target.name, e.target.value, e.target.validity.valid, "ID n°1") } name="id1" type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="proximity-filter">
                                <Form.Text className="text-muted">ID n°2 :</Form.Text>
                                <FormControl onBlur={ e => onFocusOut(formData, setFormData, e.target.name, e.target.value, e.target.validity.valid,"ID n°2" ) } name="id2" type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="proximity-filter">
                                <Form.Text className="text-muted">delta :</Form.Text>
                                <FormControl onBlur={ e => onFocusOut(formData, setFormData, e.target.name, e.target.value, e.target.validity.valid, "delta" ) } name="delta" type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" defaultValue={ DEFAULT_DELTA_VALUE } />
                            </div>
                            <div className="proximity-filter">
                                <Button className={`custom-filter-btn`} variant="outline-primary" onClick={ sendForm } >Valider</Button>
                            </div>
                            <div className="errors-container">
                                <div className={`error form-error ${ formData["id1"].error === "" ? "d-none" : ""}`}>{formData["id1"].error}</div>
                                <div className={`error form-error ${ formData["id2"].error === "" ? "d-none" : ""}`}>{formData["id2"].error}</div>
                                <div className={`error form-error ${ formData["delta"].error === "" ? "d-none" : ""}`}>{formData["delta"].error}</div>
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