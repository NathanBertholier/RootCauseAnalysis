package fr.uge.modules.api.endpoint.report;

import fr.uge.modules.api.EnvRetriever;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.ReportLinking;
import fr.uge.modules.synthetization.Synthetization;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/report/{id}")
public class ReportEndpoint {
    @Inject
    EnvRetriever envRetriever;
    private static final Logger LOGGER = Logger.getLogger(ReportEndpoint.class.getName());

    @GET
    @Produces(APPLICATION_JSON)
    public Uni<Response> getReport(
            @PathParam("id") long idLogTarget,
            @QueryParam("expanded") Boolean expanded,
            @QueryParam("delta") Long delta,
            @QueryParam("cache") Boolean cache,
            @QueryParam("proximity_limit") Integer proximity_limit,
            @QueryParam("network_size") Integer network_size
    ) {
        if(expanded == null) expanded = envRetriever.reportDefaultExpanded();
        if(delta == null) delta = envRetriever.reportDefaultDelta();
        if(cache == null) cache = envRetriever.reportDefaultCache();
        if(proximity_limit == null) proximity_limit = envRetriever.reportDefaultLimit();
        if(network_size == null) network_size = envRetriever.reportDefaultSize();

        ReportParameter reportParameter = new ReportParameter(expanded, delta, cache, proximity_limit, network_size);
        /*
        LOGGER.log(Level.INFO, "Received request for id " +  idLogTarget + " with parameters: " + reportParameter);
        System.out.println("ReportParameter: " + reportParameter);

        return Synthetization.getReport(idLogTarget, reportParameter).onItemOrFailure()
                .transform((report, error) -> {
                    if(error != null){
                        return Response.status(404).entity("Log not yet tokenized").build();
                    } else return Response.ok(report).build();
                });
         */

        return Synthetization.getReport(idLogTarget, reportParameter)
                .onItemOrFailure()
                .transform((map, error) -> {
                    if(error != null) return Response.serverError().entity(error).build();
                    else return Response.ok(map).build();
                });
    }
}
