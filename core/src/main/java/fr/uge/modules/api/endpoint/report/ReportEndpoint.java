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
import java.util.function.Function;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Endpoint used to generate reports. This endpoint needs a DB existing log.
 * Returns an error if the requested log does not exist inside the DB.
 * This endpoint also accepts parameters, according to the specs. If one of those is not provided, fetches the
 * environment variables in order to perform report generation.
 * Returns an error if the requested report is empty, meaning that the submitted parameters can not generate
 * a correct report.
 */
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
            @QueryParam("proximity_limit") Integer proximityLimit,
            @QueryParam("network_size") Integer networkSize
    ) {
        // Retrieves default report generation variables
        if(expanded == null) expanded = envRetriever.reportDefaultExpanded();
        if(delta == null) delta = envRetriever.reportDefaultDelta();
        if(proximityLimit == null) proximityLimit = envRetriever.reportDefaultLimit();
        if(networkSize == null) networkSize = envRetriever.reportDefaultSize();

        ReportParameter reportParameter = new ReportParameter(expanded, delta, proximityLimit, networkSize);

        LOGGER.info(() -> "Received request for id " + idLogTarget + " with parameters: " + reportParameter);

        return Synthetization.getReport(idLogTarget, reportParameter)
                .map(report -> {
                    LOGGER.info(() -> "Generated report: " + report);
                    return Response.ok(report).build();
                })
                // If the requested log does not exist inside the DB
                .onFailure(NotYetTokenizedError.class).recoverWithItem(withRootCauseError.apply(new NotYetTokenizedError()))
                // If the submitted parameters can not generate a correct report
                .onFailure(EmptyReportError.class).recoverWithItem(withRootCauseError.apply(new EmptyReportError()));
    }
}
