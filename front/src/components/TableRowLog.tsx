import React,{useState}  from "react";
import { Collapse } from "react-bootstrap"
import  {Log} from "../types/TokensResponse"

export const Row = ({ id, datetime, tokens, content } : Log ) => {
    const [ isUnfold, setIsUnfold ] = useState(false);

    return (
        <tr onClick={ () => setIsUnfold( !isUnfold ) } >
        <td>{id}</td>
            <td>{datetime}</td>
            <td>
                <span>{tokens.length}</span>
                { tokens.map( (token, index ) => {
                    return <Collapse in={isUnfold} key={index}>
                        <div id="example-collapse-text">
                            {token.token_type.name} : { token.value }
                        </div>
                    </Collapse>

                })}
            </td>
            <td>{content}</td>
        </tr>
    )
}