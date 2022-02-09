import React from "react";
import {useState} from "react";
import { Collapse } from "react-bootstrap"

type Token = {
    token_type : string
    value: string[]
}

type Log = {
    id: number
    dateTime: string
    tokens: Token[]
    content: string
}

export const Row = ({ id, dateTime, tokens, content } : Log ) => {
    const [ isUnfold, setIsUnfold ] = useState(false);

    return (
        <tr onClick={ () => setIsUnfold( !isUnfold ) } >
        <td>{id}</td>
            <td>{dateTime}</td>
            <td>
                <span>{tokens.length}</span>
                { tokens.map( (token, index ) => {
                    return <Collapse in={isUnfold} key={index}>
                        <div id="example-collapse-text">
                            {token.token_type} : { token.value }
                        </div>
                    </Collapse>

                })}
            </td>
            <td>{content}</td>
        </tr>
    )
}