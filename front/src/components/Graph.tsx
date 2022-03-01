import React, {useEffect, useState} from "react";
import CytoscapeComponent from "react-cytoscapejs";
import cytoscape from "cytoscape";
import {ReportResponse} from "../types/ReportResponse";
import dagre from "cytoscape-dagre";

cytoscape.use(dagre);

const stylesheet = [{
    selector: 'edge',
    style: {
        'label': 'data(label)',
        'curve-style': 'bezier'
    }
},
    {
        selector: 'node',
        style: {
            'label': 'data(id)',
            'background-color': 'data(color)'
        }
    }]

const layout = {
    name: 'dagre',
    fit: true,
    padding:300,
    animate: false,
};

export  const Graph = () => {
    const [data, setData] = useState<{nodes: cytoscape.ElementDefinition[], edges: cytoscape.ElementDefinition[]}>({ nodes: [], edges: [] });
    const [cy, setCy] = useState<cytoscape.Core | null>(null);

    useEffect(() => {
        let fakeJson = '{"root":{"id":0, "content":"test log", "datetime":"2022-02-25T18:25:43.000Z"}, "target":{"id":4, "content":"test log target", "datetime":"2022-02-25T18:25:45.000Z"}, "tokens":[{"name":"tokenname", "value":200, "count":5}], "logs":[{"id":1, "content":"GET localhost 404", "datetime":"2022-02-25T18:25:43.000Z"}, {"id":2, "content":"GET localhost 200", "datetime":"2022-02-26T19:25:43.000Z"}, {"id":3, "content":"GET localhost 201", "datetime":"2022-02-27T20:25:43.000Z"}], "proximity":[{"id":1, "links":[{"id":2, "proximity":51}, {"id":3, "proximity":51}]}, {"id":0, "links":[{"id":1, "proximity":52}, {"id":4, "proximity":52}]}]}';
        let x : ReportResponse = JSON.parse( fakeJson );
        let nodes : cytoscape.ElementDefinition[] = [];
        nodes.push( { data: { id: "ID " + x.root.id, label: "Root Cause", color: "red" } } );
        nodes.push( { data: { id: "ID " + x.target.id, label: "Cible", color: "green" } } );

        x.logs.forEach( log => {
            nodes.push( { data: { id: "ID " + log.id,  color: "gray" } } );
        })

        let edges : cytoscape.ElementDefinition[] = [];
        x.proximity.forEach( proximity => {
            proximity.links.forEach( link => {
                edges.push( { data: { source: "ID " + proximity.id, target: "ID " + link.id, label: link.proximity }} );
            } );
        });

        setData({ nodes: nodes, edges: edges } );
    }, [] );

    //Reload the layout Cytoscape
    useEffect(() => {
        if ( cy ) {
            let dagreLayout = cy.layout( layout );
            dagreLayout.run();
        }
    }, [cy] );

    return (
        <CytoscapeComponent cy={x => setCy(x)} elements={CytoscapeComponent.normalizeElements({...data})} stylesheet={stylesheet} style={{width: '100%', height: '750px'}} layout={layout} />
    )
}