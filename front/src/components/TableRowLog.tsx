import React from "react";
import {useState} from "react";
import { Collapse } from "react-bootstrap"
import {Log} from "../types/token.type";

export const Row = ({ id, datetime, tokenModels, content } : Log ) => {
    const [ isUnfold, setIsUnfold ] = useState(false);

    return (
        <tr onClick={ () => setIsUnfold( !isUnfold ) } >
        <td>{id}</td>
            <td>{datetime}</td>
            <td>
                <span>{tokenModels.length}</span>
                { tokenModels.map( (token, index ) => {
                    return <Collapse in={isUnfold} key={index}>
                        <div id="example-collapse-text">
                            {token.token_type} : { token.token_value }
                        </div>
                    </Collapse>

                })}
            </td>
            <td>{content}</td>
        </tr>
    )
}