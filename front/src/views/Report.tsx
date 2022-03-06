import {Sidebar} from "../components/Sidebar";
import React, {useEffect, useState} from "react";
import {Button, Container, Form, FormControl, Row} from "react-bootstrap";
import {Graph} from "../components/Graph";
import {toast} from "../tools/ToastManager";
import {FormData, onFocusOut, setError} from "../types/FormData";
import {ReportParams, ReportRequest} from "../types/ReportRequest";
import DataService from "../services/DataService";
import {Log} from "../types/TokensResponse";
import {ReportResponse} from "../types/ReportResponse";

const DEFAULT_ID_VALUE : number = 0;
const DEFAULT_DELTA_VALUE : number = 2;
const DEFAULT_OPTIONAL_VALUE : number = -1;

export const Report = () => {
    const [ formData, setFormData ] = useState({
        "target":       { value:DEFAULT_ID_VALUE, error:"", isRequired: true },
        "delta":        { value:DEFAULT_DELTA_VALUE, error: "", isRequired: false },
        "networkSize":  { value:DEFAULT_OPTIONAL_VALUE, error: "", isRequired: false },
        "proximity":    { value:DEFAULT_OPTIONAL_VALUE, error: "", isRequired: false }
    } as FormData );
    const [ x, setX] = useState<ReportResponse>( {
    "report":{
        "rootCause":{"id":0, "content":"test log", "datetime":"2022-02-25T18:25:43.000Z"},
        "target":{"id":4, "content":"test log target", "datetime":"2022-02-25T18:25:45.000Z"},
        "tokens":[{"name":"tokenname", "value":200, "count":5}],
        "logs":[
            {"id":1, "content":"GET localhost 404", "datetime":"2022-02-25T18:25:43.000Z"},
            {"id":2, "content":"GET localhost 200", "datetime":"2022-02-26T19:25:43.000Z"},
            {"id":3, "content":"GET localhost 201", "datetime":"2022-02-27T20:25:43.000Z"}
        ]
    },
    "proximity":[{"id":1, "links":[{"id":2, "proximity":51}, {"id":3, "proximity":51}]}, {"id":0, "links":[{"id":1, "proximity":52}, {"id":4, "proximity":52}]}]} )

    const sendForm = () => {
        if ( formData["target"].value !== DEFAULT_ID_VALUE ) {
            let params : ReportParams = { expanded: true };
            if ( formData["networkSize"].value !== DEFAULT_OPTIONAL_VALUE ) {
                params.network_size = formData["networkSize"].value;
            }
            if ( formData["proximity"].value !== DEFAULT_OPTIONAL_VALUE ) {
                params.network_size = formData["proximity"].value;
            }
            let request : ReportRequest = { params: params };
            // send data
            DataService.getReport(formData["target"].value, request).then((response: any) => {
                let a : ReportResponse = response.data
                setX( a );
            }).catch((e: Error) => {
                toast.show({
                    title: "Error",
                    content: "Un problème est survenue",
                    duration: 3000,
                });
            });
        }
        else {
            setError( formData,setFormData, "target", "*Le champs 'ID cible' est requis" );
            toast.show({
                title: "Error",
                content: "Le champs 'ID cible' est vide",
                duration: 3000,
            });
        }
    }

    return (
        <Container fluid>
            <Row>
                <Sidebar selected="report" />
                <div id="content-wrapper" >
                    <h1 className="title">Rapport</h1>
                    <Container fluid className="report-container">
                        <Row className="report-filters-container">
                            <div className="report-filter">
                                <Form.Text className="text-muted">ID cible :</Form.Text>
                                <Form.Control name="target" onBlur={ e => onFocusOut( formData, setFormData, e.target.name, e.target.value, e.target.validity.valid, "ID cible" ) } type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="report-filter">
                                <Form.Text className="text-muted">Delta de recherche :</Form.Text>
                                <FormControl name="delta" onBlur={ e => onFocusOut( formData, setFormData, e.target.name, e.target.value, e.target.validity.valid, "Delta de recherche" ) } type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="report-filter">
                                <Form.Text className="text-muted">Taille du réseau :</Form.Text>
                                <FormControl name="networkSize" onBlur={ e => onFocusOut( formData, setFormData, e.target.name, e.target.value, e.target.validity.valid, "Taille du réseau" ) } type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="report-filter">
                                <Form.Text className="text-muted">Seuil de proximité :</Form.Text>
                                <FormControl name="proximity" onBlur={ e => onFocusOut( formData, setFormData, e.target.name, e.target.value, e.target.validity.valid, "Seuil de proximité" ) } type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="report-filter">
                                <Button className={`custom-filter-btn`} variant="outline-primary" onClick={ sendForm } >Valider</Button>
                            </div>
                            <div className="errors-container">
                                <div className={`error form-error ${ formData["target"].error === "" ? "d-none" : ""}`}>{formData["target"].error}</div>
                                <div className={`error form-error ${ formData["delta"].error === "" ? "d-none" : ""}`}>{formData["delta"].error}</div>
                                <div className={`error form-error ${ formData["networkSize"].error === "" ? "d-none" : ""}`}>{formData["networkSize"].error}</div>
                                <div className={`error form-error ${ formData["proximity"].error === "" ? "d-none" : ""}`}>{formData["proximity"].error}</div>
                            </div>
                        </Row>
                        <Row>
                            <Graph res={ x } />
                        </Row>
                    </Container>
                </div>
            </Row>
        </Container>
    )
}