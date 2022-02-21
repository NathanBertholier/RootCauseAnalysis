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
    "id": -1,
    "tokenModel": {
        "token_type": 0,
        "token_value": "string"
    },
    "rows": 30
}

type LogsTableProp = {
    gettingData: (x:boolean) => void
}

export class LogsTable extends Component<LogsTableProp>{
    state = {
        list: Array<Log>()
    }

    componentDidMount() {
        this.filter( default_request );
    }

    filter( request: TokensRequest ) {
        // TODO : probably other root and format
        DataService.getAll(request).then((response: any) => {
            let logs : Log[] = response.data
            this.setState( {
                list: logs
            })
            this.props.gettingData( false );
        }).catch((e: Error) => {
            toast.show({
                content: "un problème est survenue",
                duration: 3000,
            });
            this.props.gettingData( false );
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