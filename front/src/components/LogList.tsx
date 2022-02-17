import React, {Component} from "react";
import {Table} from "react-bootstrap";
import {Row} from "./TableRowLog";
import DataService from "../services/DataService";
import {toast} from "../tools/ToastManager";
import {TokensRequest} from "../types/TokensRequest";
import {Log} from "../types/TokensResponse";

export const default_request : TokensRequest = {
    "init_datetime": "2020-06-15 00:00:00.000000",
    "end_datetime": "2020-06-16 00:00:00.000000",
    "id": 2,
    "tokenModel": {
        "token_type": 0,
        "token_value": "string"
    },
    "rows": 2
}

export class LogList extends Component{
    state = {
        list: Array<Log>()
    }

    componentDidMount() {
        this.retrieveLogs();
    }

    filter( request: TokensRequest ) {
        // TODO : probably other root and format
        DataService.getAll(request).then((response: any) => {
            console.log(response);
            let logs : Log[] = response.data
            this.setState( {
                list: logs
            })
        }).catch((e: Error) => {
            toast.show({
                content: "un problème est survenue",
                duration: 3000,
            });
        });
    }

    retrieveLogs() {
        DataService.getAll( default_request ).then((response: any) => {
            let logs : Log[] = response.data
            this.setState( {
                list: logs
            })
        }).catch((e: Error) => {
            toast.show({
                content: "un problème est survenue",
                duration: 3000,
            });
        });
    }

    render() {
        return (
            <div>
                <Table striped bordered hover className="logTable">
                    <thead>
                    <tr>
                        <th>id</th>
                        <th>DateTime</th>
                        <th>Tokens</th>
                        <th>Log</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        this.state.list.map( ( log, index ) => {
                            return  <Row id={ log.id } datetime={log.datetime} content={log.content} tokens={ log.tokens } key={index} />
                        })
                    }
                    </tbody>
                </Table>

            </div>
        )
    }
}