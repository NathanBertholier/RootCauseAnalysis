package fr.uge.modules.api.endpoint.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uge.modules.api.EnvRetriever;
import fr.uge.modules.api.model.report.GenericReport;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.error.EmptyReportError;
import fr.uge.modules.error.NotYetTokenizedError;
import fr.uge.modules.error.RootCauseError;
import fr.uge.modules.synthetization.Synthetization;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.function.Function;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/report/{id}")
public class ReportEndpoint {
    @Inject
    EnvRetriever envRetriever;
    private static final Logger LOGGER = Logger.getLogger(ReportEndpoint.class.getName());
    private static final Function<RootCauseError, Response> withRootCauseError = rootCauseError ->
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
            @QueryParam("network_size") Integer network_size,
            @QueryParam("debug") Boolean debug
    ) {
        if(expanded == null) expanded = envRetriever.reportDefaultExpanded();
        if(delta == null) delta = envRetriever.reportDefaultDelta();
        if(cache == null) cache = envRetriever.reportDefaultCache();
        if(proximity_limit == null) proximity_limit = envRetriever.reportDefaultLimit();
        if(network_size == null) network_size = envRetriever.reportDefaultSize();

        ReportParameter reportParameter = new ReportParameter(expanded, delta, cache, proximity_limit, network_size);

        LOGGER.log(Level.INFO, "Received request for id " +  idLogTarget + " with parameters: " + reportParameter);

        return Synthetization.getReport(idLogTarget, reportParameter)
                .map(report -> {
                    LOGGER.log(Level.INFO, "Generated report: {0}", report);
                    return Response.ok(report).build();
                })
                .onFailure(NotYetTokenizedError.class).recoverWithItem(withRootCauseError.apply(new NotYetTokenizedError()))
                .onFailure(EmptyReportError.class).recoverWithItem(withRootCauseError.apply(new EmptyReportError()));
    }
}
