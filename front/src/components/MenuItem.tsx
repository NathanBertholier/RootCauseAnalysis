import {Link} from "react-router-dom";
import React from "react";

type Item = {
    link: "/perf" | "/logs" | "/proximity" | "/report"
    logo: string
    text : string
    isSelected : boolean
}

export  const MenuItem = ( prop: Item ) => {
    return (
        <Link to={ prop.link } className="item">
            <div className="item-icon">
                <img src={prop.logo} className={ prop.isSelected ? "black-icon" : "" } alt="logo" />
            </div>
            <div className={ `item-text ${ prop.isSelected ? "active" : "" }` }>{ prop.text }</div>
        </Link>
    )
}