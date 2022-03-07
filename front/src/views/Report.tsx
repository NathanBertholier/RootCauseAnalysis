import {Sidebar} from "../components/Sidebar";
import React, {useState} from "react";
import {Button, Container, Form, FormControl, Row} from "react-bootstrap";
import {Graph} from "../components/Graph";
import {toast} from "../tools/ToastManager";
import {FormData, onFocusOut, setError} from "../types/FormData";
import {ReportParams, ReportRequest} from "../types/ReportRequest";
import DataService from "../services/DataService";
import {ReportResponse} from "../types/ReportResponse";

const DEFAULT_ID_VALUE : number = 0;
const DEFAULT_DELTA_VALUE : number = 2;
const DEFAULT_OPTIONAL_VALUE : number = -1;

export const Report = () => {
    const [isLoading, setIsLoading]     = useState( false );
    const [ formData, setFormData ]     = useState({
        "target":       { value:DEFAULT_ID_VALUE, error:"", isRequired: true },
        "delta":        { value:DEFAULT_DELTA_VALUE, error: "", isRequired: false },
        "networkSize":  { value:DEFAULT_OPTIONAL_VALUE, error: "", isRequired: false },
        "proximity":    { value:DEFAULT_OPTIONAL_VALUE, error: "", isRequired: false }
    } as FormData );
    const [ graph, setGraph]            = useState<ReportResponse>( {
    "report":{
        "rootCause":{"id":-1, "content":"", "datetime":""},
        "target":{"id":-1, "content":"", "datetime":""},
        "tokens":[{"name":"", "value":-1, "count":-1}],
        "logs":[]
    },
    "proximity":[]
    } )

    const sendForm = () => {
        setIsLoading( true );
        if ( formData["target"].value !== DEFAULT_ID_VALUE ) {
            let params : ReportParams = { expanded: true };
            if ( formData["networkSize"].value !== DEFAULT_OPTIONAL_VALUE ) {
                params.network_size = formData["networkSize"].value;
            }
            if ( formData["proximity"].value !== DEFAULT_OPTIONAL_VALUE ) {
                params.proximity_limit = formData["proximity"].value;
            }
            let request : ReportRequest = { params: params };

            DataService.getReport(formData["target"].value, request).then((response: any) => {
                let a : ReportResponse = response.data
                setIsLoading( false );
                setGraph( a );
            }).catch((e: Error) => {
                setIsLoading( false );
                toast.show({
                    title: "Error",
                    content: "Un problème est survenue",
                    duration: 3000,
                });
            });
        }
        else {
            setIsLoading( false );
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
                                <Form.Control name="target" onBlur={ e => onFocusOut( formData, setFormData, e.target.name, e.target.value, e.target.validity.valid, "ID cible", DEFAULT_ID_VALUE.toString() ) } type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="report-filter">
                                <Form.Text className="text-muted">Delta de recherche :</Form.Text>
                                <FormControl name="delta" onBlur={ e => onFocusOut( formData, setFormData, e.target.name, e.target.value, e.target.validity.valid, "Delta de recherche", DEFAULT_DELTA_VALUE.toString() ) } type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="report-filter">
                                <Form.Text className="text-muted">Taille du réseau :</Form.Text>
                                <FormControl name="networkSize" onBlur={ e => onFocusOut( formData, setFormData, e.target.name, e.target.value, e.target.validity.valid, "Taille du réseau", DEFAULT_OPTIONAL_VALUE.toString() ) } type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
                            </div>
                            <div className="report-filter">
                                <Form.Text className="text-muted">Seuil de proximité :</Form.Text>
                                <FormControl name="proximity" onBlur={ e => onFocusOut( formData, setFormData, e.target.name, e.target.value, e.target.validity.valid, "Seuil de proximité", DEFAULT_OPTIONAL_VALUE.toString() ) } type="text" className="custom-input" pattern="^[1-9]+[0-9]*$" />
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
                            <Graph res={ graph } isLoading={ isLoading } />
                        </Row>
                    </Container>
                </div>
            </Row>
        </Container>
    )
}