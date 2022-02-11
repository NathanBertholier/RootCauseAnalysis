import {Sidebar} from "../components/Sidebar";
import React, {useRef, useState} from "react";
import {LogList} from "../components/LogList";
import {Button, Col, Container, Dropdown, DropdownButton, Form, FormControl, Row} from "react-bootstrap";
import { AiOutlineCloseCircle } from "react-icons/ai";

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
}

/*type FilterValuesState = {
    start_date: string
    end_date: string
    token_ip: string
    status: number
    log_id: number
    rows: number
}*/

const default_filter: Item[] = [
    { id: 1, option: "start_date", text: "date début",     alone: false },
    { id: 2, option: "end_date",   text: "date fin",       alone: false },
    { id: 3, option: "token_ip",   text: "ID du token",    alone: false },
    { id: 4, option: "status",     text: "Status",         alone: false },
    { id: 5, option: "log_id",     text: "ID du log",      alone: true }
];

export const Logs = () => {
    const [ isLogIDFilter, setIsLogIDFilter ]   = useState( false );
    const [ field, setField ]                   = useState<Field[]>( [] );
    const [ filters, setFilters ]               = useState<Item[]>( default_filter );

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
                addField( { label: "date de début", type: "text", placeholder: "YYYY-MM-DD::mm", option: e } )
                break;
            case "end_date":
                addField( { label: "date de fin", type: "text", placeholder: "YYYY-MM-DD::mm", option: e } )
                break;
            case "token_ip":
                addField( { label: "Adresse IP", type: "text", placeholder: "ajouter une IP", option: e } )
                break;
            case "status":
                addField( { label: "Status", type: "number", placeholder: "", option: e } )
                break;
            case "log_id":
                addField( { label: "Log ID", type: "number", placeholder: "", option: e } )
                break;
            default:
                break;
        }
    }

    const handleDeleteField = ( e: string ) => {
        setField( prevState => prevState.filter( f => f.option !== e ) )
        let item = default_filter.find( item => item.option === e )
        setFilters( prevState => {
            let newArray = [ item as Item,...prevState ]
            return newArray.sort( (x, y) => x.id > y.id ? 1 : -1 )
        } )
    }

    const addField = ( field: Field ) => {
        setField( prevState => [...prevState, field ] )
    }

    const resetFilters = ( logIDFilter: boolean ) => {
        setField( _ => [] as Field[] )
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

    const filterLogs = () => {
        console.log( startDateInput.current.value );
        console.log( endDateInput.current.value );
        console.log( tokenIPInput.current.value );
        console.log( statusInput.current.value );
        console.log( rowsNumberInput.current.value );
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
                                                    field.map( ( field, index ) => {
                                                        return <Row key={index}>
                                                            <Col sm={2} className="input-labels" >
                                                                { field.label }
                                                            </Col>
                                                            <Col sm={9} className="filter-container">
                                                                <FormControl ref={ getInputRef( field ) } type={ field.type } placeholder={field.placeholder} />
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
                                <Form.Text className="text-muted">Nombre de ligne :</Form.Text>
                                <Form.Control ref={rowsNumberInput} type="number" className="custom-input"  min="1" />
                                <Button className="custom-filter-btn" variant="outline-primary" onClick={ () => filterLogs() }>Valider</Button>
                            </Col>
                        </Row>
                    </Container>
                    <LogList />
                </div>
            </Row>
        </Container>
    )
}