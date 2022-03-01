import React from "react";

type InputField = {
    value: number
    error: string
    isRequired: boolean
}

export type FormData = {
    [key: string]: InputField
}

export let onFocusOut = function ( data: FormData, setter: React.Dispatch<React.SetStateAction<FormData>>, key: string, value: string, valid: boolean, field: string) {
    if ( value === "" && data[ key ].isRequired ) {
        setError( data, setter, key, "*Le champs "+ field +" est vide" );
        return;
    }
    else if ( !valid ) {
        setError( data, setter, key, "*Le champs "+ field +" ne prend que des nombres positives" );
        return;
    }
    let copy = {...data}
    copy[ key ].value = parseInt(value)
    copy[ key ].error = ""
    setter( copy )
}

export const setError = ( data: FormData, setter: React.Dispatch<React.SetStateAction<FormData>>, key: string, message: string ) => {
    let copy = {...data}
    copy[ key ].error = message
    setter( copy );
}