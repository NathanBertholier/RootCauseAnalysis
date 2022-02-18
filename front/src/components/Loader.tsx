import React from "react";
import {Spinner} from "react-bootstrap";

type LoaderProps ={
    show: boolean
}

export  const Loader = ({ show } : LoaderProps ) => {
    return (
        <div className={ `spinner-overlay ${show ? "" : "d-none"}` }>
            <Spinner animation="border" variant="light" />
            <span className="loader-message">Loading ...</span>
        </div>
    )
}