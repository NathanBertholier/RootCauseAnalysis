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
import java.text.MessageFormat;
import java.util.function.Function;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.text.MessageFormat.*;
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

    @GET
    @Produces(APPLICATION_JSON)
    public Uni<Response> getReport(
            @PathParam("id") long idLogTarget,
            @QueryParam("expanded") Boolean expanded,
            @QueryParam("delta") Long delta,
            @QueryParam("proximity_limit") Integer proximity_limit,
            @QueryParam("network_size") Integer network_size
    ) {
        if(expanded == null) expanded = envRetriever.reportDefaultExpanded();
        if(delta == null) delta = envRetriever.reportDefaultDelta();
        if(proximity_limit == null) proximity_limit = envRetriever.reportDefaultLimit();
        if(network_size == null) network_size = envRetriever.reportDefaultSize();

        ReportParameter reportParameter = new ReportParameter(expanded, delta, proximity_limit, network_size);

        LOGGER.info(() -> "Received request for id " + idLogTarget + " with parameters: " + reportParameter);

        return Synthetization.getReport(idLogTarget, reportParameter)
                .map(report -> {
                    LOGGER.info(() -> "Generated report: " + report);
                    return Response.ok(report).build();
                })
                .onFailure(NotYetTokenizedError.class).recoverWithItem(withRootCauseError.apply(new NotYetTokenizedError()))
                .onFailure(EmptyReportError.class).recoverWithItem(withRootCauseError.apply(new EmptyReportError()));
    }
}
