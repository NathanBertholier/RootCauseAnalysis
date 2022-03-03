import {Sidebar} from "../components/Sidebar";
import React, {useEffect, useRef, useState} from "react";
import {LogsTable, default_request} from "../components/LogsTable";
import {Button, Col, Container, Dropdown, DropdownButton, Form, FormControl, Row} from "react-bootstrap";
import { AiOutlineCloseCircle } from "react-icons/ai";
import {toast} from "../tools/ToastManager";
import DateTimePicker from 'react-datetime-picker';
import DataService from "../services/DataService";
import {TokenModel, TokensRequest} from "../types/TokensRequest";
import { Loader } from "../components/Loader"

type Item = {
    id: number
    option: "start_date" | "end_date" | "ipv4" | "ipv6" | "statut" | "edgeResponse" | "log_id"
    text: string
    alone: boolean
}

enum Filter {
    START_DATE,
    END_DATE,
    EDGE_RESPONSE,
    IPv4,
    IPv6,
    STATUT,
    LOG_ID
}

type DateTimeInput = {
    id: number
    type: "datetime"
    label: string
    value: Date
    setter: (field: Field[], date: Date) => void
    error?: string
}

type Input = {
    id: number
    type: string
    label: string
    placeholder: string
    patern: string
    ref: React.MutableRefObject<HTMLInputElement>
    error?: string
    validator: ( field: Field[] ) => void
}

type Field = DateTimeInput | Input

export const Logs = () => {
    const [ DEFAULT_FILTERS, setDEFAULT_FILTERS ]           = useState<Item[]>( [] );
    // ui
    const [ isLogIDFilter, setIsLogIDFilter ]               = useState( false );
    const [ isBtnDisabled, setIsBtnDisabled ]               = useState( true );
    const [ rowInputError, setRowInputError ]               = useState( "" );
    const [ uiFields, setUiFields ]                         = useState<Field[]>( [] );
    const [ filters, setFilters ]                           = useState<Item[]>( DEFAULT_FILTERS );

    // event
    const [ shouldApplyFilters, setShouldApplyFilters ]     = useState( false );
    const [ requestData, setRequestData ]                   = useState<TokensRequest>( default_request );

    // input value
    const [ startDate, setStartDate]    = useState(new Date());
    const [ endDate, setEndDate]        = useState(new Date());
    const tokenIPInput                  = useRef<HTMLInputElement>(null!);
    const tokenIPv6Input                = useRef<HTMLInputElement>(null!);
    const statusInput                   = useRef<HTMLInputElement>(null!);
    const edgeResponseInput             = useRef<HTMLInputElement>(null!);
    const logIDInput                    = useRef<HTMLInputElement>(null!);
    const rowsNumberInput               = useRef<HTMLInputElement>(null!);

    const DEFAULT_FIELDS : Field[] = [
        {id: Filter.START_DATE, type: "datetime", label: "Date de début", value: startDate, setter: (data,date) => {
            updateDateTimeValueField( data, Filter.START_DATE, date );
            setStartDate( date );
        },},
        {id: Filter.END_DATE, type: "datetime", label: "Date de fin", value: endDate, setter: (data,date) => {
            updateDateTimeValueField( data, Filter.END_DATE, date );
            setEndDate( date )
        } },
        {id: Filter.EDGE_RESPONSE, type: "text", label: "Edge Response", placeholder: "", patern: "^((Hit)|(RefreshHit)|(Miss)|(LimitExceeded)|(CapacityExceeded)|(Error)|(Redirect))$", validator: data => {
            let message = getErrorMessage( "Edge Response", edgeResponseInput )
            setErrorMessage( data, Filter.EDGE_RESPONSE, message );
        }, ref: edgeResponseInput },
        {id: Filter.IPv4, type: "text", label: "Adresse IP v4", placeholder: "IP v4", patern: "(([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])\\.){3}([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])", validator: data => {
            let message = getErrorMessage( "Adresse IP v4", tokenIPInput )
            setErrorMessage( data, Filter.IPv4, message );
        }, ref: tokenIPInput},
        {id: Filter.IPv6, type: "text", label: "Adresse IP v6", placeholder: "IP v6", patern: "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))", validator: data => {
            let message = getErrorMessage( "Adresse IP v6", tokenIPv6Input )
            setErrorMessage( data, Filter.IPv6, message );
        }, ref: tokenIPv6Input},
        {id: Filter.STATUT, type: "text", label: "Status", placeholder: "", patern: "([1-5][0-5][0-9])", validator: data => {
            let message = getErrorMessage( "Statut", statusInput )
            setErrorMessage( data, Filter.STATUT, message );
        }, ref: statusInput},
        {id: Filter.LOG_ID, type: "text", label: "Log ID", placeholder: "", patern: "[0-9]*", validator: data => {
            let message = getErrorMessage( "Log ID", logIDInput )
            setErrorMessage( data, Filter.LOG_ID, message );
        }, ref: logIDInput }
    ]

    /******************************************************
     | Event
     *****************************************************/

    // Select le mode de filtrage et retire dans la liste de selection de filtre et ajoute sur la liste des champs
    const handleSelect = (e:string | null) => {
        setFilters( prevState => {
            if ( e === "log_id" && !isLogIDFilter ) {
                prevState = DEFAULT_FILTERS
                resetUIFields( true );
            }
            else if ( isLogIDFilter ) {
                prevState = DEFAULT_FILTERS
                resetUIFields( false );
            }
            return prevState.filter(f => f.option !== e);
        } )

        // ajoute sur la liste des champs
        switch ( e ) {
            case "start_date":
                addField( DEFAULT_FIELDS[ Filter.START_DATE ] )
                break;
            case "end_date":
                addField( DEFAULT_FIELDS[ Filter.END_DATE ] )
                break;
            case "ipv4":
                addField( DEFAULT_FIELDS[ Filter.IPv4 ] );
                break;
            case "ipv6":
                addField( DEFAULT_FIELDS[ Filter.IPv6 ] );
                break;
            case "statut":
                addField( DEFAULT_FIELDS[ Filter.STATUT ] )
                break;
            case "edgeResponse":
                addField( DEFAULT_FIELDS[ Filter.EDGE_RESPONSE ] )
                break;
            case "log_id":
                addField( DEFAULT_FIELDS[ Filter.LOG_ID ] )
                break;
            default:
                break;
        }
    }

    // Supprime le champs et insert dans la liste de selection des filtres
    const handleDeleteField = ( e: number ) => {
        setUiFields( prevState => prevState.filter( f => f.id !== e ) )

        // TODo : improve this part
        let item = DEFAULT_FILTERS.find( item => item.id === e )
        setFilters( prevState => {
            let newArray = [ item as Item,...prevState ]
            return newArray.sort( (x, y) => x.id > y.id ? 1 : -1 )
        })
    }

    // verifie les champs, construit le json pour l'API et met a jour la liste de log
    const applyFilters = () => {
        setIsBtnDisabled( true );

        if ( !handleValidation() ) {
            toast.show({
                title: "Error",
                content: "Le formulaire contient une ou plusieurs erreurs",
                duration: 3000,
            });
            setIsBtnDisabled( false );
            return;
        }

        let start_datetime : string = (uiFields.find( field => field.id === Filter.START_DATE ) !== undefined) ? getDate( startDate ) : "";
        let end_datetime : string   = (uiFields.find( field => field.id === Filter.END_DATE ) !== undefined) ? getDate( endDate ) : "";
        let id_log : number         = logIDInput.current !== null ? parseInt( logIDInput.current.value ) : -1;
        let rowsNumber : number     = rowsNumberInput.current !== null ? parseInt( rowsNumberInput.current.value ) : 30;

        let tokens: TokenModel[] = [];
        if ( tokenIPInput.current !== null ) {
            tokens.push( { token_type: 1, token_value: tokenIPInput.current.value } )
        }
        if ( tokenIPv6Input.current !== null ) {
            tokens.push( { token_type: 2, token_value: tokenIPv6Input.current.value } )
        }

        if ( statusInput.current !== null ) {
            tokens.push( { token_type: 3, token_value: statusInput.current.value } )
        }

        if ( edgeResponseInput.current !== null ) {
            tokens.push( { token_type: 5, token_value: edgeResponseInput.current.value } )
        }

        let obj : TokensRequest = {
            init_datetime: start_datetime,
            end_datetime: end_datetime,
            id: id_log,
            tokens: tokens,
            rows: rowsNumber
        }

        setRequestData( obj );
        setShouldApplyFilters( true );
    }

    // check rows Input is valid
    const onRowsInputFocusOut = () : boolean => {
        if ( checkIsEmpty(rowsNumberInput) ) {
            setRowInputError( "* Le champ 'Nombre de ligne' est vide" );
            return false;
        }
        else if ( checkIsNotValid(rowsNumberInput) ) {
            setRowInputError( "* Le champ 'Nombre de ligne' doit être compris entre 1 et 100" );
            return false
        }
        setRowInputError( "" );
        return true;
    }

    /******************************************************
     |  fields & filters array
     *****************************************************/

    // Ajout dans la liste des champs
    const addField = ( field: Field ) => {
        setUiFields( prevState => [...prevState, field ] )
    }

    const updateDateTimeValueField = ( data: Field[], fieldId: Filter, value: Date ) => {
        let copy = [...data];
        let index: number = copy.findIndex( x => x.id === fieldId );
        (copy[ index ] as DateTimeInput).value = value;
        setUiFields( copy )
    }

    // set l'erreur pour un champs spécifique
    const setErrorMessage = ( data: Field[], filterID: Filter, message: string ) => {
        let newArray = [...data];
        newArray.map( field => field.error = (field.id === filterID) ? message : field.error );
        setUiFields( newArray );
    }

    // remise à zéro de la liste des champs
    const resetUIFields = ( logIDFilter: boolean ) => {
        setUiFields( _ => [] as Field[] )
        setIsLogIDFilter( logIDFilter );
    }

    /******************************************************
     |  Validation
     *****************************************************/

    const checkIsEmpty = ( input :  React.MutableRefObject<HTMLInputElement> ) : boolean => {
        return input.current !== null && input.current.value === "";
    }

    const checkIsNotValid = (input :  React.MutableRefObject<HTMLInputElement>) : boolean => {
        return input.current !== null && !input.current.validity.valid;
    }

    const handleValidation = () => {
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
            // STATUS
            if ( checkIsEmpty(statusInput) ) {
                formIsValid = false;
                setErrorMessage( uiFields, Filter.STATUT, "* Le champ 'Status' est vide" );
            }
            else if ( checkIsNotValid( statusInput ) ) {
                formIsValid = false;
                setErrorMessage( uiFields, Filter.STATUT, "* Le champ 'Status' ne prend que des nombres à 3 chiffres" );
            }

            // IP - v4
            if ( checkIsEmpty(tokenIPInput) ) {
                formIsValid = false;
                setErrorMessage( uiFields, Filter.IPv4, "* Le champ 'Adresse IP' est vide" );
            }
            else if ( checkIsNotValid(tokenIPInput) ) {
                formIsValid = false;
                setErrorMessage( uiFields, Filter.IPv4, "* Le champ 'Adresse IP' est incorrect" );
            }
        }

        if ( !onRowsInputFocusOut() ) {
            formIsValid = false;
        }

        return formIsValid;
    }

    /******************************************************
     | Tools
     *****************************************************/

    // recupere la date en format YY-MM-DD hh:mm:ss
    const getDate = ( date: Date ) : string => {
        return date.toISOString( ).split("T")[0] + " " + date.toLocaleTimeString( "fr-FR" );
    }

    const getErrorMessage = ( fieldName: string, input: React.MutableRefObject<HTMLInputElement> ) : string => {
        let message = ""
        if ( input.current !== null && input.current.value === "" ) {
            message = "* Le champ '"+ fieldName +"' est vide"
        }
        else if (input.current !== null && !input.current.validity.valid) {
            message = "* Le champ '"+ fieldName +"' est incorrect"
        }
        return message
    }

    /******************************************************
     |  View
     *****************************************************/

    const getInput = ( field: Field ) => {
        if (field.id === Filter.START_DATE || field.id === Filter.END_DATE) {
            let inputField = field as DateTimeInput;
            return <DateTimePicker onChange={v => inputField.setter(uiFields, v) } value={ inputField.value } format="yyyy-MM-dd hh:mm:ss a" />
        }
        let inputField = field as Input;
        return <FormControl ref={ inputField.ref } type={ inputField.type } pattern={ inputField.patern } placeholder={inputField.placeholder} onBlur={ () => inputField.validator( uiFields ) } />
    }

    // Similar to componentDidMount : create the default filter array
    useEffect(() => {
        DataService.getTokenTypes().then((response: any) => {
            let obj : string[] = response.data

            let array : Item[] = []
            obj.map( s => {
                switch ( s ) {
                    case "Datetime":
                        array.push( { id: Filter.START_DATE,    option: "start_date",   text: "Date début",     alone: false } )
                        array.push( { id: Filter.END_DATE,      option: "end_date",     text: "Date fin",       alone: false }, )
                        break;
                    case "IPv4":
                        array.push( { id: Filter.IPv4,          option: "ipv4",         text: s,                alone: false } )
                        break;
                    case "IPv6":
                        array.push( { id: Filter.IPv6,          option: "ipv6",         text: s,                alone: false } )
                        break;
                    case "Statut":
                        array.push( { id: Filter.STATUT,        option: "statut",       text: s,                alone: false } )
                        break;
                    case "EdgeResponse":
                        array.push( { id: Filter.EDGE_RESPONSE, option: "edgeResponse", text: s,                alone: false } )
                        break;
                    default:
                        break;
                }
                return s
            } )
            array.push( { id: Filter.LOG_ID,                    option: "log_id",       text: "ID du log",      alone: true } )
            array.sort( (x, y) => x.id > y.id ? 1 : -1 )
            setDEFAULT_FILTERS( array )
        }).catch(() => {
            toast.show({
                title: "Error",
                content: "Un problème est survenue",
                duration: 3000,
            });
        });
    }, []);

    useEffect(() => {
        setFilters( DEFAULT_FILTERS );
    }, [DEFAULT_FILTERS] );

    return (
        <Container fluid>
            <Row>
                <Sidebar selected="logs" />
                <div id="content-wrapper" >
                    <h1 className="title" >Logs</h1>
                    <Container fluid className="logs-container">
                        <Row className="logs-filters-container" >
                            <Col sm={8}>
                                <Container fluid className="p-0">
                                    <Row>
                                        <Col lg={10}>
                                            <Container fluid className="p-0">
                                                {
                                                    uiFields.map( ( field, index ) => {

                                                        return <Row key={index}>
                                                            <Col sm={3} className="input-labels" >
                                                                { field.label }
                                                            </Col>
                                                            <Col sm={8} className="filter-container" >
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
                                            <Form.Control ref={rowsNumberInput} type="text" className="custom-input" pattern="^[1-9][0-9]?$|^100$" onBlur={ onRowsInputFocusOut } defaultValue="30" />
                                            <Button className={`custom-filter-btn ${isBtnDisabled ? "disabled" : ""}`} variant="outline-primary" onClick={ () => applyFilters() } disabled={ isBtnDisabled } >Valider</Button>
                                        </Col>
                                    </Row>
                                    <Row className={ rowInputError !== "" ? "" : "d-none" } >
                                        <div className="error row-number-error" >{ rowInputError }</div>
                                    </Row>
                                </Container>
                            </Col>
                        </Row>
                        <Row className="logs-table-container" >
                                <Loader show={ isBtnDisabled } />
                                <LogsTable ref={logList => {
                                    if ( shouldApplyFilters && logList !== null ) {
                                        logList.filter( requestData );
                                        setShouldApplyFilters( false );
                                    }
                                } } gettingData={ isGettingData => setIsBtnDisabled( isGettingData ) } />
                        </Row>
                    </Container>
                </div>
            </Row>
        </Container>
    )
}