import {Sidebar} from "../components/Sidebar";
import React, {useRef, useState} from "react";
import {LogList, default_request} from "../components/LogList";
import {Button, Col, Container, Dropdown, DropdownButton, Form, FormControl, Row} from "react-bootstrap";
import { AiOutlineCloseCircle } from "react-icons/ai";
import {RequestData} from "../types/token.type"
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
}

type Field = DateTimeInput | Input

const default_filter: Item[] = [
    { id: 1, option: "start_date", text: "date début",     alone: false },
    { id: 2, option: "end_date",   text: "date fin",       alone: false },
    { id: 3, option: "token_ip",   text: "ID du token",    alone: false },
    { id: 4, option: "status",     text: "Status",         alone: false },
    { id: 5, option: "log_id",     text: "ID du log",      alone: true }
];

export const Logs = () => {
    // ui
    const [ startDate, setStartDate]                        = useState(new Date());
    const [ endDate, setEndDate]                            = useState(new Date());
    const [ isLogIDFilter, setIsLogIDFilter ]               = useState( false );
    const [ isBtnDisabled, setIsBtnDisabled ]               = useState( false );
    const [ hasNumberOfRowError, setHasNumberOfRowError ]   = useState( false );
    const [ uiFields, setUiFields ]                         = useState<Field[]>( [] );
    const [ filters, setFilters ]                           = useState<Item[]>( default_filter );

    // event
    const [ shouldApplyFilters, setShouldApplyFilters ]     = useState( false );
    const [ requestData, setRequestData ]                   = useState<RequestData>( default_request );

    //const startDateInput    = useRef<HTMLInputElement>(null!);
    //const endDateInput      = useRef<HTMLInputElement>(null!);
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
                addField( {id: Filter.START_DATE, type: "datetime", label: "date de début", value: startDate, setter: (date: Date) => setStartDate( date ) } )
                break;
            case "end_date":
                addField( {id: Filter.END_DATE, type: "datetime", label: "date de fin", value: endDate, setter: (date: Date) => setEndDate( date ) } )
                break;
            case "token_ip":
                addField( {id: Filter.TOKEN_IP, type: "text", label: "Adresse IP", placeholder: "ajouter une IP", patern: "[0-9]*" } )
                break;
            case "status":
                addField( {id: Filter.STATUS, type: "number", label: "Status", placeholder: "", patern: "[0-9]{3}" } )
                break;
            case "log_id":
                addField( {id: Filter.LOG_ID, type: "number", label: "Log ID", placeholder: "", patern: "[0-9]*" } )
                break;
            default:
                break;
        }
    }

    const handleDeleteField = ( e: number ) => {
        setUiFields( prevState => prevState.filter( f => f.id !== e ) )

        //TODO :: need change
        /*let item = default_filter.find( item => item.option === e )
        setFilters( prevState => {
            let newArray = [ item as Item,...prevState ]
            return newArray.sort( (x, y) => x.id > y.id ? 1 : -1 )
        } )*/
    }

    const addField = ( field: Field ) => {
        setUiFields( prevState => [...prevState, field ] )
    }

    const resetFilters = ( logIDFilter: boolean ) => {
        setUiFields( _ => [] as Field[] )
        setIsLogIDFilter( logIDFilter );
    }

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

    const getDate = ( date: Date ) : string => {
        return date.toISOString( ).split("T")[0] + " " + date.toLocaleTimeString( "fr-FR" );
    }

    const applyFilters = () => {
        setIsBtnDisabled( true );

        if ( !handleValidation() ) {
            toast.show({
                content: "Le formulaire contient une ou plusieurs erreurs",
                duration: 3000,
            });
            setIsBtnDisabled( false );
            return;
        }

        //let start_datetime : string = startDateInput.current !== null ? startDateInput.current.value : "";
        //let end_datetime : string   = endDateInput.current !== null ? endDateInput.current.value : "";
        let id_log : number         = logIDInput.current !== null ? parseInt( logIDInput.current.value ) : 0;
        let rowsNumber : number     = rowsNumberInput.current !== null ? parseInt( rowsNumberInput.current.value ) : 0;

        let obj : RequestData = {
            init_datetime: "start_datetime",
            end_datetime: "end_datetime",
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
        setShouldApplyFilters( true );
    }
    
    const handleValidation = () => {
        if ( uiFields.length === 0 ) return false;
        let formIsValid = true;

        if ( isLogIDFilter ) {
            if ( logIDInput.current === null || logIDInput.current.value === "" ) {
                formIsValid = false;
                setErrorMessage( Filter.LOG_ID, "* Le champ 'Log ID' doit contenir une valeur" );
            }
            else if ( !logIDInput.current.validity.valid ) {
                formIsValid = false;
                setErrorMessage( Filter.LOG_ID, "* Le champ 'Log ID' ne prend que des nombres" );
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

    const setErrorMessage = ( filter: Filter, message: string ) => {
        uiFields.map( field => field.error = field.id === filter ? message : field.error )
    }

    const getInput = ( field: Field ) => {
        if (field.id === Filter.START_DATE || field.id === Filter.END_DATE) {
            let inputField = field as DateTimeInput;
            return <DateTimePicker onChange={inputField.setter} value={ inputField.value } format="yyyy-MM-dd hh:mm:ss a" />
        }
        let inputField = field as Input;
        return <FormControl ref={ getInputRef( inputField ) } type={ inputField.type } pattern={ inputField.patern } placeholder={inputField.placeholder} />
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