import React, {Component} from "react";
import {Log} from "../types/token.type"
import {Table} from "react-bootstrap";
import {Row} from "./TableRowLog";
import DataService from "../services/DataService";

export class LogList extends Component{
    state = {
        list: Array<Log>()
    }

    componentDidMount() {
        this.retrieveLogs();
    }

    retrieveLogs() {
        DataService.getAll({
            "init_datetime": "2021-11-20T00:00:05.000",
            "end_datetime": "2021-11-20T00:20:00.000",
            "id": 54,
            "tokens": [
                {
                    "token_type": "IP",
                    "token_value": "10.16.27.62.244"
                }
            ],
            "rows": 100
        }).then((response: any) => {
            let obj : Log[] = JSON.parse( JSON.stringify(response.data[0].logDemonstrators) )
            this.setState( {
                list: obj
            })
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