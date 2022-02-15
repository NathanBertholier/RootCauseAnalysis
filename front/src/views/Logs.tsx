import {Sidebar} from "../components/Sidebar";
import React, {useEffect, useRef, useState} from "react";
import {LogList, default_request} from "../components/LogList";
import {Button, Col, Container, Dropdown, DropdownButton, Form, FormControl, Row} from "react-bootstrap";
import { AiOutlineCloseCircle } from "react-icons/ai";
import {RequestData, Token} from "../types/token.type"
import {toast} from "../tools/ToastManager";
import DateTimePicker from 'react-datetime-picker';

type Item = {
    id: number
    option: "start_date" | "end_date" | "token_ip" | "status" | "log_id"
    text: string
    alone: boolean
}

enum Filter {
    START_DATE,
    END_DATE,
    TOKEN_IP,
    STATUS,
    LOG_ID
}

type DateTimeInput = {
    id: number
    type: "datetime"
    label: string
    value: Date
    setter: (date: Date) => void
    error?: string
}

type Input = {
    id: number
    type: string
    label: string
    placeholder: string
    patern: string
    error?: string
    validator: ( field: Field[] ) => void
}

type Field = DateTimeInput | Input

const default_filter: Item[] = [
    { id: Filter.START_DATE,    option: "start_date", text: "date début",     alone: false },
    { id: Filter.END_DATE,      option: "end_date",   text: "date fin",       alone: false },
    { id: Filter.TOKEN_IP,      option: "token_ip",   text: "IP du token",    alone: false },
    { id: Filter.STATUS,        option: "status",     text: "Status",         alone: false },
    { id: Filter.LOG_ID,        option: "log_id",     text: "ID du log",      alone: true }
];

export const Logs = () => {
    // ui
    const [ isLogIDFilter, setIsLogIDFilter ]               = useState( false );
    const [ isBtnDisabled, setIsBtnDisabled ]               = useState( false );
    const [ hasNumberOfRowError, setHasNumberOfRowError ]   = useState( false );
    const [ uiFields, setUiFields ]                         = useState<Field[]>( [] );
    const [ filters, setFilters ]                           = useState<Item[]>( default_filter );

    // event
    const [ shouldApplyFilters, setShouldApplyFilters ]     = useState( false );
    const [ requestData, setRequestData ]                   = useState<RequestData>( default_request );

    // input value
    const [ startDate, setStartDate]    = useState(new Date());
    const [ endDate, setEndDate]        = useState(new Date());
    const tokenIPInput                  = useRef<HTMLInputElement>(null!);
    const statusInput                   = useRef<HTMLInputElement>(null!);
    const logIDInput                    = useRef<HTMLInputElement>(null!);
    const rowsNumberInput               = useRef<HTMLInputElement>(null!);

    /******************************************************
     | Event
     *****************************************************/

    // Select le mode de filtrage et retire dans la liste de selection de filtre et ajoute sur la list de champs
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
                addField( {id: Filter.START_DATE, type: "datetime", label: "date de début", value: startDate, setter: (date: Date) => setStartDate( date ) } )
                break;
            case "end_date":
                addField( {id: Filter.END_DATE, type: "datetime", label: "date de fin", value: endDate, setter: (date: Date) => setEndDate( date ) } )
                break;
            case "token_ip":
                addField( {id: Filter.TOKEN_IP, type: "text", label: "Adresse IP", placeholder: "ajouter une IP", patern: "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b", validator: data => {
                    let message = "";
                    if ( tokenIPInput.current !== null && tokenIPInput.current.value === "" ) {
                        message = "* Le champ 'Adresse IP' est vide";
                    }
                    else if (tokenIPInput.current !== null && !tokenIPInput.current.validity.valid) {
                        message = "* Le champ 'Adresse IP' est incorrect";
                    }
                    setErrorMessage( data, Filter.TOKEN_IP, message );
                }});
                break;
            case "status":
                addField( {id: Filter.STATUS, type: "text", label: "Status", placeholder: "", patern: "[0-9][0-9][0-9]", validator: data => {
                        let message = "";
                        if ( statusInput.current !== null && statusInput.current.value === "" ) {
                            message = "* Le champ 'Status' est vide";
                        }
                        else if (statusInput.current !== null && !statusInput.current.validity.valid) {
                            message = "* Le champ 'Status' est incorrect";
                        }
                        setErrorMessage( data, Filter.STATUS, message );
                } } )
                break;
            case "log_id":
                addField( {id: Filter.LOG_ID, type: "text", label: "Log ID", placeholder: "", patern: "[0-9]*", validator: () => {

                } } )
                break;
            default:
                break;
        }

        console.log( uiFields );
    }

    // Supprime le champs et insert dans la liste de selection des filtres
    const handleDeleteField = ( e: number ) => {
        setUiFields( prevState => prevState.filter( f => f.id !== e ) )

        let item = default_filter.find( item => item.id === e )
        setFilters( prevState => {
            let newArray = [ item as Item,...prevState ]
            return newArray.sort( (x, y) => x.id > y.id ? 1 : -1 )
        })
    }

    const applyFilters = () => {
        setIsBtnDisabled( true );

        if ( !handleValidation() ) {
            toast.show({
                content: "Le formulaire contient une ou plusieurs erreurs",
                duration: 3000,
            });
            setIsBtnDisabled( false );
        }

        let start_datetime : string = (uiFields.find( field => field.id === Filter.START_DATE ) !== undefined) ? getDate( startDate ) : "";
        let end_datetime : string   = (uiFields.find( field => field.id === Filter.END_DATE ) !== undefined) ? getDate( endDate ) : "";
        let id_log : number         = logIDInput.current !== null ? parseInt( logIDInput.current.value ) : 0;
        let rowsNumber : number     = rowsNumberInput.current !== null ? parseInt( rowsNumberInput.current.value ) : 0;

        let tokens: Token[] = [];
        if ( tokenIPInput.current !== null ) {
            tokens.push( { token_type: "IP", token_value: tokenIPInput.current.value } );
        }
        if ( statusInput.current !== null ) {
            tokens.push( { token_type: "Status", token_value: statusInput.current.value } );
        }

        let obj : RequestData = {
            init_datetime: start_datetime,
            end_datetime: end_datetime,
            id: id_log,
            tokens: tokens,
            rows: rowsNumber
        }

        setRequestData( obj );
        //setShouldApplyFilters( true );
    }

    const getDate = ( date: Date ) : string => {
        return date.toISOString( ).split("T")[0] + " " + date.toLocaleTimeString( "fr-FR" );
    }

    /******************************************************
     |  fields array
     *****************************************************/

    const addField = ( field: Field ) => {
        setUiFields( prevState => [...prevState, field ] )
    }

    const resetFilters = ( logIDFilter: boolean ) => {
        setUiFields( _ => [] as Field[] )
        setIsLogIDFilter( logIDFilter );
    }

    const setErrorMessage = ( data: Field[], filterID: Filter, message: string ) => {
        let newArray = [...data];
        newArray.map( field => field.error = (field.id === filterID) ? message : field.error );
        setUiFields( newArray );
    }

    /******************************************************
     |  Validation
     *****************************************************/

    const checkInputIsNumber = ( input :  React.MutableRefObject<HTMLInputElement> ) : boolean => {
        return !( input.current !== null && ( !input.current.validity.valid || parseInt(input.current.value) <= 0) );
    }

    const handleValidation = () => {
        if ( uiFields.length === 0 ) return false;
        let formIsValid = true;

        if ( isLogIDFilter ) {
            if ( logIDInput.current === null || logIDInput.current.value === "" ) {
                formIsValid = false;
                setErrorMessage( uiFields, Filter.LOG_ID, "* Le champ 'Log ID' doit contenir une valeur" );
            }
            else if ( !logIDInput.current.validity.valid ) {
                formIsValid = false;
                setErrorMessage( uiFields, Filter.LOG_ID, "* Le champ 'Log ID' ne prend que des nombres" );
            }
        }
        else {
            console.log( "check status" )
            if ( !checkInputIsNumber(statusInput) ) {
                console.log( "set false" );
                formIsValid = false;
                setErrorMessage( uiFields, Filter.STATUS, "* Le champ 'Status' ne prend que des nombres à 3 chiffres" );
            }
            if ( tokenIPInput.current !== null && !tokenIPInput.current.validity.valid ) {
                formIsValid = false;
                setErrorMessage( uiFields, Filter.TOKEN_IP, "* Le champ 'Adresse IP' est incorrect" );
            }
        }

        if ( !checkInputIsNumber(rowsNumberInput) ) {
            formIsValid = false;
            setHasNumberOfRowError( true );
        }
        else {
            setHasNumberOfRowError( false );
        }

        return formIsValid;
    }

    /******************************************************
     |  HTML
     *****************************************************/

    const getInputRef = ( field: Field ) => {
        switch ( field.id ) {
            case Filter.TOKEN_IP:
                return tokenIPInput
            case Filter.STATUS:
                return statusInput
            case Filter.LOG_ID:
                return logIDInput
            default:
                return null;
        }
    }

    const getInput = ( field: Field ) => {
        if (field.id === Filter.START_DATE || field.id === Filter.END_DATE) {
            let inputField = field as DateTimeInput;
            return <DateTimePicker onChange={inputField.setter} value={ inputField.value } format="yyyy-MM-dd hh:mm:ss a" />
        }
        let inputField = field as Input;
        return <FormControl ref={ getInputRef( inputField ) } type={ inputField.type } pattern={ inputField.patern } placeholder={inputField.placeholder} onBlur={ () => inputField.validator( uiFields ) } />
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
                                                            <Col sm={9} className="filter-container" >
                                                                <Container fluid className="p-0">
                                                                    <Row>
                                                                        { getInput( field ) }
                                                                    </Row>
                                                                    <Row className={ `${ field.error === "" ? "d-none": "" }` } >
                                                                        <div className="error form-error">{ field.error }</div>
                                                                    </Row>
                                                                </Container>
                                                            </Col>
                                                            <Col sm={1}>
                                                                <Button variant="outline-danger" key={index} onClick={ () => handleDeleteField( field.id ) }><AiOutlineCloseCircle /></Button>
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
                                            <Button className={`custom-filter-btn ${isBtnDisabled ? "disabled" : ""}`} variant="outline-primary" onClick={ () => applyFilters() } disabled={ isBtnDisabled } >Valider</Button>
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
                        if ( shouldApplyFilters && logList !== null ) {
                            logList.filter( requestData );
                            setShouldApplyFilters( false );
                            setIsBtnDisabled( false );
                        }
                    } } />
                </div>
            </Row>
        </Container>
    )
}