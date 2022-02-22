import React from "react";
import {Table} from "react-bootstrap";

type PrimitiveType = string | Symbol | number | boolean
type TableHeaders<T extends MinTableItem> = Record<keyof T, string>
type CustomRenderers<T extends MinTableItem> = Partial< Record<keyof T, (it: T) => React.ReactNode> >

// helper to get an array containing the object values with the correct type infered.
function objectValues<T extends {}>( obj: T ) {
    return Object.keys( obj ).map( key => obj[ key as keyof T] );
}

function objectKey<T extends {}>( obj: T ) {
    return Object.keys( obj ).map( key => key as keyof T );
}

function isPrimitive( value: any ) : value is PrimitiveType {
    return (
      typeof value === "string" || typeof value === "number" || typeof value === "boolean" || typeof value === "symbol"
    );
}

interface MinTableItem {
}

interface TableProps<T extends MinTableItem> {
    items: T[]
    headers:TableHeaders<T>
    customRenderers: CustomRenderers<T>
    TableclassName?: string
}

//https://fernandoabolafio.medium.com/generic-table-component-with-react-and-typescript-d849ad9f4c48
export default function GenericTable<T extends MinTableItem>( props: TableProps<T> ) {
    function renderRow( item: T, index: number ) {
        return (
            <tr key={ index } >
                {objectKey(item).map( (property, propertyIndex) => {
                    const customRenderer = props.customRenderers?.[property];

                    if (customRenderer) {
                        return <td key={ propertyIndex }>{customRenderer(item)}</td>;
                    }

                    return (
                        <td key={ propertyIndex }>{isPrimitive(item[property]) ? item[property] : ""}</td>
                    );
                })}
            </tr>
        )
    }

    return (
        <div>
            <Table striped bordered hover className={props.TableclassName} >
                <thead>
                <tr>
                    {
                        objectValues( props.headers ).map( (header, index) => {
                            return <th key={ index } >{ header }</th>
                        })
                    }
                </tr>
                </thead>
                <tbody>{props.items.map( renderRow )}</tbody>
            </Table>
            <div className={`nodata-table ${ props.items.length !== 0 ? "d-none" : ""}`}>{props.items.length === 0 ? "No data": ""}</div>
        </div>
    )
}