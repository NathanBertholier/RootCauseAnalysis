import {Sidebar} from "../components/Sidebar";
import React, {useRef, useState} from "react";
import {LogList, default_request} from "../components/LogList";
import {Button, Col, Container, Dropdown, DropdownButton, Form, FormControl, Row} from "react-bootstrap";
import { AiOutlineCloseCircle } from "react-icons/ai";
import {RequestData} from "../types/token.type"
import {toast} from "../tools/ToastManager";

type Item = {
    id: number
    option: "start_date" | "end_date" | "token_ip" | "status" | "log_id"
    text: string
    alone: boolean
}

type Field = {
    label: string
    option: "start_date" | "end_date" | "token_ip" | "status" | "log_id"
    type: string
    placeholder: string
    patern: string
    error?: string
}

const default_filter: Item[] = [
    { id: 1, option: "start_date", text: "date début",     alone: false },
    { id: 2, option: "end_date",   text: "date fin",       alone: false },
    { id: 3, option: "token_ip",   text: "ID du token",    alone: false },
    { id: 4, option: "status",     text: "Status",         alone: false },
    { id: 5, option: "log_id",     text: "ID du log",      alone: true }
];

export const Logs = () => {
    // ui
    const [ isLogIDFilter, setIsLogIDFilter ]               = useState( false );
    const [ isBtnDisabled, setIsBtnDisabled ]               = useState( false );
    const [ hasNumberOfRowError, setHasNumberOfRowError ]   = useState( false );
    const [ uiFields, setUiFields ]                         = useState<Field[]>( [] );
    const [ filters, setFilters ]                           = useState<Item[]>( default_filter );

    // event
    const [ sendFilter, setSendFilter ]                     = useState( false );
    const [ requestData, setRequestData ]                   = useState<RequestData>( default_request );

    const startDateInput    = useRef<HTMLInputElement>(null!);
    const endDateInput      = useRef<HTMLInputElement>(null!);
    const tokenIPInput      = useRef<HTMLInputElement>(null!);
    const statusInput       = useRef<HTMLInputElement>(null!);
    const logIDInput        = useRef<HTMLInputElement>(null!);
    const rowsNumberInput   = useRef<HTMLInputElement>(null!);

    const handleSelect = (e:string | null) => {
        setFilters( prevState => {
            if ( e === "log_id" && !isLogIDFilter ) {
                prevState = default_filter
                resetFilters( true );
            }
            else if ( isLogIDFilter ) {
                prevState = default_filter
                resetFilters( false );
            }
            return prevState.filter(f => f.option !== e);
        } )

        switch ( e ) {
            case "start_date":
                addField( { label: "date de début", type: "text", placeholder: "YYYY-MM-DD::mm", option: e, patern: "[0-9]*" } )
                break;
            case "end_date":
                addField( { label: "date de fin", type: "text", placeholder: "YYYY-MM-DD::mm", option: e, patern: "[0-9]*" } )
                break;
            case "token_ip":
                addField( { label: "Adresse IP", type: "text", placeholder: "ajouter une IP", option: e, patern: "[0-9]*" } )
                break;
            case "status":
                addField( { label: "Status", type: "number", placeholder: "", option: e, patern: "[0-9]*" } )
                break;
            case "log_id":
                addField( { label: "Log ID", type: "number", placeholder: "", option: e, patern: "[0-9]*" } )
                break;
            default:
                break;
        }
    }

    const handleDeleteField = ( e: string ) => {
        setUiFields( prevState => prevState.filter( f => f.option !== e ) )
        let item = default_filter.find( item => item.option === e )
        setFilters( prevState => {
            let newArray = [ item as Item,...prevState ]
            return newArray.sort( (x, y) => x.id > y.id ? 1 : -1 )
        } )
    }

    const addField = ( field: Field ) => {
        setUiFields( prevState => [...prevState, field ] )
    }

    const resetFilters = ( logIDFilter: boolean ) => {
        setUiFields( _ => [] as Field[] )
        setIsLogIDFilter( logIDFilter );
    }

    const getInputRef = ( field: Field ) => {
        switch ( field.option ) {
            case "start_date":
                return startDateInput
            case "end_date":
                return endDateInput
            case "token_ip":
                return tokenIPInput
            case "status":
                return statusInput
            case "log_id":
                return logIDInput
            default:
                return null;
        }
    }

    const filterLogs = (event : React.MouseEvent<HTMLButtonElement> ) => {
        setIsBtnDisabled( true );

        if ( !handleValidation() ) {
            toast.show({
                content: "Le formulaire contient une ou plusieurs erreurs",
                duration: 3000,
            });
            setIsBtnDisabled( false );
            return;
        }

        let start_datetime : string = startDateInput.current !== null ? startDateInput.current.value : "";
        let end_datetime : string   = endDateInput.current !== null ? endDateInput.current.value : "";
        let id_log : number         = logIDInput.current !== null ? parseInt( logIDInput.current.value ) : 0;
        let rowsNumber : number     = rowsNumberInput.current !== null ? parseInt( rowsNumberInput.current.value ) : 0;

        let obj : RequestData = {
            init_datetime: start_datetime,
            end_datetime: end_datetime,
            id: id_log,
            tokens: [
                {
                    token_type: "IP",
                    token_value: "10.16.27.62.244"
                }
            ],
            rows: rowsNumber
        }

        setRequestData( obj );
        setSendFilter( true );
    }
    
    const handleValidation = () => {
        if ( uiFields.length === 0 ) return false;
        let formIsValid = true;

        if ( isLogIDFilter ) {
            if ( logIDInput.current === null || logIDInput.current.value === "" ) {
                formIsValid = false;
                setErrorMessage( "log_id", "* Le champ 'Log ID' doit contenir une valeur" );
            }
            else if ( !logIDInput.current.validity.valid ) {
                formIsValid = false;
                setErrorMessage( "log_id", "* Le champ 'Log ID' ne prend que des nombres" );
            }
        }
        else {

        }

        if ( rowsNumberInput.current !== null && ( !rowsNumberInput.current.validity.valid || parseInt(rowsNumberInput.current.value) <= 0) ) {
            formIsValid = false;
            setHasNumberOfRowError( true );
        }
        else {
            setHasNumberOfRowError( false );
        }
        
        return formIsValid;
    }

    const setErrorMessage = ( filter: string, message: string ) => {
        uiFields.map( field => field.error = field.option === filter ? message : field.error )
    }

    return (
        <Container fluid>
            <Row>
                <Sidebar selected="logs" />
                <div id="content-wrapper" >
                    <h1 className="title" >Logs</h1>
                    <Container fluid>
                        <Row>
                            <Col sm={8}>
                                <Container fluid className="p-0">
                                    <Row>
                                        <Col lg={10}>
                                            <Container fluid className="p-0">
                                                {
                                                    uiFields.map( ( field, index ) => {
                                                        return <Row key={index}>
                                                            <Col sm={2} className="input-labels" >
                                                                { field.label }
                                                            </Col>
                                                            <Col sm={9} className="filter-container">
                                                                <Container fluid className="p-0">
                                                                    <Row>
                                                                        <FormControl ref={ getInputRef( field ) } type={ field.type } pattern={ field.patern } placeholder={field.placeholder} />
                                                                    </Row>
                                                                    <Row className={ `${ field.error === "" ? "d-none": "" }` } >
                                                                        <div className="error form-error">{ field.error }</div>
                                                                    </Row>
                                                                </Container>
                                                            </Col>
                                                            <Col sm={1}>
                                                                <Button variant="outline-danger" key={index} onClick={ () => handleDeleteField( field.option ) }><AiOutlineCloseCircle /></Button>
                                                            </Col>
                                                        </Row>
                                                    })
                                                }
                                            </Container>
                                        </Col>
                                        <Col lg={2}>
                                            <DropdownButton
                                                className={`add-filter-btn`}
                                                variant="outline-secondary"
                                                title="Ajout Filtre"
                                                id="custom-log-dropdown"
                                                align="end"
                                                onSelect={ handleSelect } >
                                                {
                                                    filters.map( ( item, index ) => {
                                                        if ( item.alone && filters.length !== 1 ) {
                                                            return <div key={index} ><Dropdown.Divider /><Dropdown.Item eventKey={item.option}>{ item.text }</Dropdown.Item></div>
                                                        }
                                                        return <Dropdown.Item eventKey={item.option} key={index}>{ item.text }</Dropdown.Item>
                                                    })
                                                }
                                            </DropdownButton>
                                        </Col>
                                    </Row>
                                </Container>
                            </Col>
                            <Col sm={4}>
                                <Container fluid className="p-0 number-row-filter-container">
                                    <Row>
                                        <Col>
                                            <Form.Text className="text-muted">Nombre de ligne :</Form.Text>
                                            <Form.Control ref={rowsNumberInput} type="number" className="custom-input"  min="1" />
                                            <Button className={`custom-filter-btn ${isBtnDisabled ? "disabled" : ""}`} variant="outline-primary" onClick={ event => filterLogs( event ) } disabled={ isBtnDisabled } >Valider</Button>
                                        </Col>
                                    </Row>
                                    <Row className={ hasNumberOfRowError ? "" : "d-none" } >
                                        <div className="error" >* Le champ 'Log ID' ne prend que des nombres</div>
                                    </Row>
                                </Container>
                            </Col>
                        </Row>
                    </Container>
                    <LogList ref={ logList => {
                        if ( sendFilter && logList !== null ) {
                            logList.filter( requestData );
                            setSendFilter( false );
                            setIsBtnDisabled( false );
                        }
                    } } />
                </div>
            </Row>
        </Container>
    )
}