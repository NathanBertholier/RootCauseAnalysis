package fr.uge.modules.api.endpoint.report;

import fr.uge.modules.api.EnvRetriever;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.error.EmptyReportError;
import fr.uge.modules.error.NotYetTokenizedError;
import fr.uge.modules.error.RootCauseError;
import fr.uge.modules.synthetization.Synthetization;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/report/{id}")
public class ReportEndpoint {
    @Inject
    EnvRetriever envRetriever;
    private static final Logger LOGGER = Logger.getLogger(ReportEndpoint.class.getName());
    private static final Function<RootCauseError, Response> withStatusAndError = rootCauseError ->
            Response
                    .status(rootCauseError.getStatus())
                    .entity(rootCauseError.getMessage())
                    .build();

    static {
        LOGGER.addHandler(new ConsoleHandler());
    }

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
        LOGGER.log(Level.INFO, "Received request for id {0} with parameters: {1}",  new Object[]{ idLogTarget, reportParameter});

        return Synthetization.getReport(idLogTarget, reportParameter)
                .map(reportResponse -> Response.ok(reportResponse).build())
                .onFailure(NotYetTokenizedError.class)
                    .recoverWithItem(withStatusAndError.apply(new NotYetTokenizedError()))
                .onFailure(EmptyReportError.class)
                    .recoverWithItem(withStatusAndError.apply(new EmptyReportError()));
    }
}
