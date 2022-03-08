import React from "react";

type InputField = {
    value: number
    error: string
    isRequired: boolean
}

export type FormData = {
    [key: string]: InputField
}

export let onFocusOut = function ( data: FormData, setter: React.Dispatch<React.SetStateAction<FormData>>, key: string, value: string, valid: boolean, field: string, default_value: string) {
    if ( value === "" && data[ key ].isRequired ) {
        setError( data, setter, key, "* The field "+ field +" is empty" );
        return;
    }
    else if ( !valid ) {
        setError( data, setter, key, "* The field "+ field +" only takes positive numbers" );
        return;
    }

    let copy = {...data}
    copy[ key ].value = parseInt((value === "") ? default_value : value)
    copy[ key ].error = ""
    setter( copy )
}

export const setError = ( data: FormData, setter: React.Dispatch<React.SetStateAction<FormData>>, key: string, message: string ) => {
    let copy = {...data}
    copy[ key ].error = message
    setter( copy );
}