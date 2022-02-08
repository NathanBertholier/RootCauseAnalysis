import React from "react";

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
    return (
        <tr  >
            <td>{id}</td>
            <td>{dateTime}</td>
            <td>
                <span>{tokens.length}</span>
                { tokens.map( (token, index ) => {
                    return <div><span>{token.token_type} : </span><span>{ token.value }</span></div>
                })}
            </td>
            <td>{content}</td>
        </tr>
    )
}