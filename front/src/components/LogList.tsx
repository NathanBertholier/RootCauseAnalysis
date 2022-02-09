import React from "react";
import { Table } from "react-bootstrap"
import Data from '../data.json'
import {Row} from "../components/TableRowLog"

export const LogList = () => {
    console.log( Data );
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
                { Data.map( (log, index ) => {
                    return  <Row id={ log.id } dateTime={log.datetime} content={log.content} tokens={ log.tokens } key={index} />
                })}
                </tbody>
            </Table>

        </div>
    )
}