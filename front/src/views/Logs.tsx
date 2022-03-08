import {Sidebar} from "../components/Sidebar";
import React, {useRef, useState} from "react";
import {LogsTable, default_request} from "../components/LogsTable";
import {Button, Col, Container, Dropdown, DropdownButton, Form, FormControl, Row} from "react-bootstrap";
import { AiOutlineCloseCircle } from "react-icons/ai";
import {toast} from "../tools/ToastManager";
import DateTimePicker from 'react-datetime-picker';
import {TokenModel, TokensRequest} from "../types/TokensRequest";
import { Loader } from "../components/Loader"

enum FilterType {
    START_DATE,
    END_DATE,
    EDGE_RESPONSE,
    IPv4,
    IPv6,
    STATUT,
    LOG_ID
}

type DateTimeInput = {
    label: string
    value: Date
    setter: (filters: Filter[], date: Date) => void
    error?: string
}

type Input = {
    label: string
    ref: React.MutableRefObject<HTMLInputElement>
    placeholder: string
    patern: string
    validator: ( filters: Filter[] ) => void
    error?: string
}

type Field = DateTimeInput | Input

type Filter = {
    id: FilterType
    text: string
    isSelected: boolean
    alone: boolean
    formField: Field
}

export const Logs = () => {
    // ui
    const [ isLogIDFilter, setIsLogIDFilter ]               = useState( false );
    const [ isBtnDisabled, setIsBtnDisabled ]               = useState( true );
    const [ rowInputError, setRowInputError ]               = useState( "" );

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

    const [ filters, setFilters ]           = useState<Filter[]>( [
        {
            id: FilterType.START_DATE,
            text:"Start date",
            isSelected: false,
            alone:false,
            formField: {
                label:"Start date",
                value: startDate,
                setter: (data, date) => {
                    updateDateTimeValueField( data, FilterType.START_DATE, date );
                    setStartDate( date );
                }
            }
        },
        {
            id: FilterType.END_DATE,
            text:"End date",
            isSelected: false,
            alone:false,
            formField: {
                label:"End date",
                value: endDate,
                setter: (data, date) => {
                    updateDateTimeValueField( data, FilterType.END_DATE, date );
                    setEndDate( date )
                }
            }
        },
        {
            id: FilterType.IPv4,
            text:"IPv4",
            isSelected: false,
            alone:false,
            formField: {
                label:"IPv4 address",
                ref: tokenIPInput,
                placeholder: "192.168.1.1",
                patern: "(([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])\\.){3}([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])",
                validator: data => {
                    let message = getErrorMessage( "IPv4 address", tokenIPInput )
                    setErrorMessage( data, FilterType.IPv4, message );
                }
            }
        },
        {
            id: FilterType.IPv6,
            text:"IPv6",
            isSelected: false,
            alone:false,
            formField: {
                label: "IPv6 address",
                ref: tokenIPv6Input,
                placeholder: "IP v6",
                patern: "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))",
                validator: data => {
                    let message = getErrorMessage( "IPv6 address", tokenIPv6Input )
                    setErrorMessage( data, FilterType.IPv6, message );
                }
            }
        },
        {
            id: FilterType.STATUT,
            text:"Status",
            isSelected: false,
            alone:false,
            formField: {
                label: "Status",
                ref: statusInput,
                placeholder: "404",
                patern: "([1-5][0-5][0-9])",
                validator: data => {
                    let message = getErrorMessage( "Status", statusInput )
                    setErrorMessage( data, FilterType.STATUT, message );
                }
            }
        },
        {
            id: FilterType.EDGE_RESPONSE,
            text:"EdgeResponse",
            isSelected: false,
            alone:false,
            formField: {
                label: "Edge Response",
                ref: edgeResponseInput,
                placeholder: "Miss | Hit | RefreshHit | LimitExceeded | CapacityExceeded | Error | Redirect",
                patern: "^((Hit)|(RefreshHit)|(Miss)|(LimitExceeded)|(CapacityExceeded)|(Error)|(Redirect))$",
                validator: data => {
                    let message = getErrorMessage( "Edge Response", edgeResponseInput )
                    setErrorMessage( data, FilterType.EDGE_RESPONSE, message );
                }
            }
        },
        {
            id: FilterType.LOG_ID,
            text:"Log ID",
            isSelected: false,
            alone:true,
            formField: {
                label: "Log ID",
                ref: logIDInput,
                placeholder: "",
                patern: "[0-9]*",
                validator: data => {
                    let message = getErrorMessage( "Log ID", logIDInput )
                    setErrorMessage( data, FilterType.LOG_ID, message );
                }
            }

        }
    ] );

    /******************************************************
     | Event
     *****************************************************/

    // select filter, if log ID is selected, all the other filter will be unselected
    const handleSelect = (e:string | null) => {
        if ( e ) {
            let selectedID = parseInt( e );
            if ( selectedID === FilterType.LOG_ID  ) {
                filters.forEach( filter => filter.isSelected = false );
                setIsLogIDFilter( true );
            }
            else {
                if ( isLogIDFilter ) {
                    filters.forEach( filter => filter.isSelected = false );
                }
                setIsLogIDFilter( false );
            }

            setSelectedFilter( selectedID, true );
        }
    }

    // Unselect filter with ID e
    const handleUnSelectFilter = ( e: number ) => {
        setSelectedFilter( e, false );
        if (e === FilterType.LOG_ID) {
            setIsLogIDFilter( false );
        }
    }

    // verify filter fields, build JSON for API and trigger update table
    const applyFilters = () => {
        setIsBtnDisabled( true );

        if ( !handleValidation() ) {
            toast.show({
                title: "Error",
                content: "The form contains one or more errors",
                duration: 3000,
            });
            setIsBtnDisabled( false );
            return;
        }

        let startDateFilter = filters.find( filter => filter.id === FilterType.START_DATE );
        let endDateFilter   = filters.find( filter => filter.id === FilterType.END_DATE );

        let start_datetime : string = (startDateFilter && startDateFilter.isSelected) ? getDate( startDate ) : "";
        let end_datetime : string   = (endDateFilter && endDateFilter.isSelected) ? getDate( endDate ) : "";
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
    const rowsInputIsValid = () : boolean => {
        if ( checkIsEmpty(rowsNumberInput) ) {
            setRowInputError( "* The field 'Number of lines' is empty" );
            return false;
        }
        else if ( checkIsNotValid(rowsNumberInput) ) {
            setRowInputError( "* The field 'Number of lines' must be between 1 and 100" );
            return false
        }
        setRowInputError( "" );
        return true;
    }

    /******************************************************
     |  Getter & Setter
     *****************************************************/

    const setSelectedFilter = (id: FilterType, value: boolean ) => {
        let copy = [...filters];
        let index = copy.findIndex( filter => filter.id === id );
        if ( index !== -1 ) {
            copy[index].isSelected = value;
            setFilters( copy );
        }
    }

    const updateDateTimeValueField = (data: Filter[], fieldId: FilterType, value: Date ) => {
        let copy = [...data];
        let index: number = copy.findIndex( x => x.id === fieldId );
        if ( index !== -1 ) {
            (copy[index].formField as DateTimeInput).value = value
            setFilters( copy )
        }
    }

    // set the error message for a specific filter id
    const setErrorMessage = (data: Filter[], filterID: FilterType, message: string ) => {
        let newArray = [...data];
        newArray.forEach( filter => filter.formField.error = (filter.id === filterID) ? message : filter.formField.error );
        setFilters( newArray );
    }

    // recupere la date en format YY-MM-DD hh:mm:ss
    const getDate = ( date: Date ) : string => {
        return date.toISOString( ).split("T")[0] + " " + date.toLocaleTimeString( "fr-FR" );
    }

    /******************************************************
     |  Validation
     *****************************************************/

    const checkIsEmpty = ( input : React.MutableRefObject<HTMLInputElement> ) : boolean => {
        return input.current !== null && input.current.value === "";
    }

    const checkIsNotValid = (input : React.MutableRefObject<HTMLInputElement>) : boolean => {
        return input.current !== null && !input.current.validity.valid;
    }

    const getErrorMessage = ( fieldName: string, input: React.MutableRefObject<HTMLInputElement> ) : string => {
        let message = ""
        if ( checkIsEmpty( input ) ) {
            message = "* The field '"+ fieldName +"' is empty"
        }
        else if (checkIsNotValid( input )) {
            message = "* The field '"+ fieldName +"' is incorrect"
        }
        return message
    }

    const checkTextField = ( input : React.MutableRefObject<HTMLInputElement>, filterID: FilterType, fieldLabel: string ): boolean => {
        let message = getErrorMessage( fieldLabel, input );
        setErrorMessage( filters, filterID, message );
        return message === "";
    }

    const checkDatetimeField = ( input : Date, filterID: FilterType, fieldLabel: string ): boolean => {
        let filter = filters.find( f => f.id === filterID );
        if (filter && filter.isSelected && input === null) {
            setErrorMessage( filters, filterID, "* The field '"+fieldLabel+"' is incorrect" );
            return false;
        }

        return true
    }

    const handleValidation = () => {
        let formIsValid = true;

        if ( isLogIDFilter ) {
            if ( logIDInput.current === null || logIDInput.current.value === "" ) {
                formIsValid = false;
                setErrorMessage( filters, FilterType.LOG_ID, "* The field 'Log ID' must contain a value" );
            }
            else if ( !logIDInput.current.validity.valid ) {
                formIsValid = false;
                setErrorMessage( filters, FilterType.LOG_ID, "* The field 'Log ID' only takes numbers" );
            }
        }
        else {
            if ( !checkTextField( statusInput, FilterType.STATUT, "Status" ) ||
                 !checkTextField( tokenIPInput, FilterType.IPv4, "IPv4 address" ) ||
                 !checkTextField( tokenIPv6Input, FilterType.IPv6, "IPv6 address" ) ||
                 !checkTextField( edgeResponseInput, FilterType.EDGE_RESPONSE, "Edge Response" ) ||
                 !checkDatetimeField( startDate, FilterType.START_DATE, "Start date" ) ||
                 !checkDatetimeField( endDate, FilterType.END_DATE, "End date" )
            ) {
                formIsValid = false
            }
        }

        if ( !rowsInputIsValid() ) {
            formIsValid = false;
        }

        return formIsValid;
    }

    /******************************************************
     |  View
     *****************************************************/

    const getInput = ( filter: Filter ) => {
        if (filter.id === FilterType.START_DATE || filter.id === FilterType.END_DATE) {
            let dateTimeInput = filter.formField as DateTimeInput;
            return <DateTimePicker onChange={v => dateTimeInput.setter(filters, v) } value={ dateTimeInput.value } format="yyyy-MM-dd hh:mm:ss a" />
        }
        let input = filter.formField as Input;
        return <FormControl ref={ input.ref } type="text" pattern={ input.patern } placeholder={input.placeholder} onBlur={ () => input.validator( filters ) } />
    }

    return (
        <Container fluid>
            <Row>
                <Sidebar selected="logs" />
                <div id="content-wrapper" >
                    <h1 className="title" >Logs</h1>
                    <Container fluid className="logs-container">
                        <Row className="logs-filters-container" >
                            <Col lg={8}>
                                <Container fluid className="p-0">
                                    <Row>
                                        <Col xl={10}>
                                            <Container fluid className="p-0">
                                                {
                                                    filters.filter( filter => filter.isSelected ).map( (filter, index) => {
                                                        return <Row key={index} className="filter-container">
                                                            <Col xl={3} sm={12} className="input-labels" >
                                                                { filter.formField.label }
                                                            </Col>
                                                            <Col xl={7}
                                                                 sm={10}>
                                                                <Container fluid className="p-0">
                                                                    <Row>
                                                                        { getInput( filter ) }
                                                                    </Row>
                                                                    <Row className={ `${ filter.formField.error === "" ? "d-none": "" }` } >
                                                                        <div className="error form-error">{ filter.formField.error }</div>
                                                                    </Row>
                                                                </Container>
                                                            </Col>
                                                            <Col sm={2}>
                                                                <Button variant="outline-danger" key={index} onClick={ () => handleUnSelectFilter( filter.id ) }><AiOutlineCloseCircle /></Button>
                                                            </Col>
                                                        </Row>
                                                    } )
                                                }
                                            </Container>
                                        </Col>
                                        <Col xl={2}>
                                            <DropdownButton
                                                className={`add-filter-btn`}
                                                variant="outline-secondary"
                                                title="Add Filter"
                                                id="custom-log-dropdown"
                                                align="end"
                                                onSelect={ handleSelect } >
                                                {
                                                    filters.filter( filter => !filter.isSelected ).map( ( filter, index ) => {
                                                        if ( filter.alone ) {
                                                            return <div key={index} ><Dropdown.Divider /><Dropdown.Item eventKey={filter.id}>{ filter.text }</Dropdown.Item></div>
                                                        }
                                                        return <Dropdown.Item eventKey={filter.id} key={index}>{ filter.text }</Dropdown.Item>
                                                    })
                                                }
                                            </DropdownButton>
                                        </Col>
                                    </Row>
                                </Container>
                            </Col>
                            <Col lg={4}>
                                <Container fluid className="p-0 number-row-filter-container">
                                    <Row>
                                        <Col>
                                            <Form.Text className="text-muted">Number of rows :</Form.Text>
                                            <Form.Control ref={rowsNumberInput} type="text" className="custom-input" pattern="^[1-9][0-9]?$|^100$" onBlur={ rowsInputIsValid } defaultValue="30" />
                                            <Button className={`custom-filter-btn ${isBtnDisabled ? "disabled" : ""}`} variant="outline-primary" onClick={ () => applyFilters() } disabled={ isBtnDisabled } >Validate</Button>
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