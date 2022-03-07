import React, {useEffect, useState} from "react";
import CytoscapeComponent from "react-cytoscapejs";
import cytoscape from "cytoscape";
import {Log, MostUsedToken, ReportResponse} from "../types/ReportResponse";
import { Loader } from "./Loader"
const fcose = require( "cytoscape-fcose" )

cytoscape.use(fcose);

const stylesheet : cytoscape.Stylesheet[] = [{
    selector: 'edge',
    style: {
        'label': 'data(label)',
        'curve-style': 'bezier',
        'target-arrow-shape': 'triangle',
        'color': '#AAAEBA'
    }
},
{
    selector: "edge[label]",
    style: {
        "text-background-color": "white",
        "text-background-opacity": 1
    }
},
{
    selector: 'node',
    style: {
        'label': 'data(id)',
        'background-color': 'data(color)',
        'text-halign': 'center',
        'text-valign': 'center',
        'padding-top': '10px'
    }
}
]

const layout = {
    name: 'fcose',
    fit: true,
    padding:200,
    idealEdgeLength: 200,
};

type GraphProp = {
    res: ReportResponse
    isLoading: boolean
}

export  const Graph = ({res, isLoading} : GraphProp ) => {
    const [data, setData]                       = useState<{nodes: cytoscape.ElementDefinition[], edges: cytoscape.ElementDefinition[]}>({ nodes: [], edges: [] });
    const [mostUsedTokens, setMostUsedTokens]   = useState( [] as MostUsedToken[] );
    const [logToolTip, setLogToolTip]           = useState<Log>({} as Log);

    useEffect(() => {
        let nodes : cytoscape.ElementDefinition[] = [];
        if ( res.proximity.length > 0 ) {
            nodes.push( { data: { id: res.report.rootCause.id.toString(), label: "Root Cause", color: "#F24E1E", log: res.report.rootCause } } );
            nodes.push( { data: { id: res.report.target.id.toString(), label: "Target", color: "#4ECB71",log: res.report.target } } );
        }

        res.report.logs.filter( log => log.id !== res.report.rootCause.id ).forEach( log => {
            nodes.push( { data: { id: log.id.toString(),  color: "#C4C4C4",log: log } } );
        })

        let edges : cytoscape.ElementDefinition[] = [];
        res.proximity.forEach( proximity => {
            proximity.links.forEach( link => {
                edges.push( { data: { source: proximity.id, target: link.id, label: link.proximity }} );
            } );
        });

        setMostUsedTokens( res.report.tokens )
        setData({ nodes: nodes, edges: edges } );
    }, [res] );

    const setCy = (core: cytoscape.Core) => {
        core.removeListener("tap");
        core.on('tap', 'node', evt => {
            setLogToolTip( evt.target.data().log );
        });

        // Reload Layout
        let dagreLayout = core.layout( layout );
        dagreLayout.run();
    }

    return (
        <div className="cytoscape-container">
            <div className="most-use-tokens">
                <table>
                    <thead>
                        <tr>
                            <th>Token type</th>
                            <th>Value</th>
                            <th>Count</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            mostUsedTokens.map( ( mostUsed, index ) => {
                                return <tr key={index} >
                                    <td className="tokens-labels">{mostUsed.name}</td>
                                    <td className="tokens-values">{mostUsed.value}</td>
                                    <td className="tokens-counts">{mostUsed.count}</td>
                                </tr>
                            } )
                        }
                    </tbody>
                </table>
            </div>
            <div className={`toolTip ${Object.keys(logToolTip).length === 0  ? "d-none" : ""}`} >
                <div><span className="toolTip-title">ID : </span><span className="toolTip-value">{logToolTip.id}</span></div>
                <div><span className="toolTip-entity">Log : </span><span className="toolTip-value">{logToolTip.content}</span></div>
                <div><span className="toolTip-entity">DateTime : </span><span className="toolTip-value">{logToolTip.datetime}</span></div>
            </div>
            <div className="legend">
                <div className="legend-title">Legend</div>
                <div className="hint-container">
                    <div className="hint hint-node hint-orange"/>
                    <div className="hint-label">Root cause</div>
                </div>
                <div className="hint-container">
                    <div className="hint hint-node hint-green"/>
                    <div className="hint-label">Target Log</div>
                </div>
                <div className="hint-container">
                    <div className="hint hint-node hint-gray">8</div>
                    <div className="hint-label">Log ID</div>
                </div>
                <div className="hint-container">
                    <div className="hint hint-edge">84</div>
                    <div className="hint-label">Proximity weight</div>
                </div>
            </div>
            <CytoscapeComponent minZoom={0.1} wheelSensitivity={0.2} cy={x => setCy(x)} elements={CytoscapeComponent.normalizeElements(data)} stylesheet={stylesheet} style={{width: '100%', height: '700px'}} layout={layout} />
            <Loader show={isLoading} />
        </div>
    )
}