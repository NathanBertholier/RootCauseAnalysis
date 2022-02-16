import React, {Component} from "react";
import {Log, RequestData} from "../types/token.type"
import {Table} from "react-bootstrap";
import {Row} from "./TableRowLog";
import DataService from "../services/DataService";
import {toast} from "../tools/ToastManager";

export const default_request: RequestData = {
    "init_datetime": "2021-11-20T00:00:05.000",
    "end_datetime": "2021-11-20T00:20:00.000",
    "id": 54,
    "tokens": [
        {
            "token_type": "IP",
            "token_value": "10.16.27.62.244"
        }
    ],
    "rows": 30
};

export class LogList extends Component{
    state = {
        list: Array<Log>()
    }

    componentDidMount() {
        this.retrieveLogs();
    }

    filter( request: RequestData ) {
        // TODO : probably other root and format
        DataService.getAll(request).then((response: any) => {
            let obj : Log[] = JSON.parse( JSON.stringify(response.data[0].logDemonstrators) )
            this.setState( {
                list: obj
            })
            console.log( obj );
            console.log("update");
        }).catch((e: Error) => {
            toast.show({
                content: "un problème est survenue",
                duration: 3000,
            });
        });
    }

    retrieveLogs() {
        DataService.getAll( default_request ).then((response: any) => {
            let obj : Log[] = JSON.parse( JSON.stringify(response.data[0].logDemonstrators) )
            this.setState( {
                list: obj
            })
        }).catch((e: Error) => {
            toast.show({
                content: "un problème est survenue",
                duration: 3000,
            });
        });
    }

    render() {
        const { list } = this.state;
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
                        list.map( ( log, index ) => {
                            return  <Row id={ log.id } datetime={log.datetime} content={log.content} tokenModels={ log.tokenModels } key={index} />
                        })
                    }
                    </tbody>
                </Table>

            </div>
        )
    }
}