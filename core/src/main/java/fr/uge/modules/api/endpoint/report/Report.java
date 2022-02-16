package fr.uge.modules.api.endpoint.report;

import fr.uge.modules.api.model.ReportResponse;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.synthetization.Synthetization;
import io.smallrye.mutiny.Uni;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/report/{id}")
public class Report {
    private static final Logger LOGGER = Logger.getLogger(Report.class.getName());

    @GET
    @Produces(APPLICATION_JSON)
    public Uni<Response> getReport(
            @PathParam("id") long idLogTarget,
            @QueryParam("expanded") boolean expanded,
            @QueryParam("delta") long delta,
            @QueryParam("cache") boolean cache,
            @QueryParam("proximity_limit") float proximity_limit,
            @QueryParam("network_size") int network_size
    ) throws SQLException {
        ReportParameter reportParameter = new ReportParameter(expanded, delta, cache, proximity_limit, network_size);
        LOGGER.log(Level.INFO, "Received request for id " +  idLogTarget + " with parameters: " + reportParameter);
        var report = Synthetization.getReport(idLogTarget, reportParameter);
        return Uni.createFrom().item(Response.ok().entity("Response incoming").build());
    }
}
