package fr.uge.modules.api.endpoint.report;

import fr.uge.modules.api.model.ReportResponse;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.Linking;
import fr.uge.modules.synthetization.Synthetization;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import javax.ws.rs.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/report/{id}")
public class Report {
    private static final Logger LOGGER = Logger.getLogger(Report.class.getName());

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Blocking
    public Uni<ReportResponse> getReport(@PathParam("id")long idLogTarget,
                                         @QueryParam("expanded") boolean expanded,
                                         @QueryParam("delta") long delta,
                                         @QueryParam("cache") boolean cache,
                                         @QueryParam("proximity_limit") float proximity_limit,
                                         @QueryParam("network_size") int network_size) throws SQLException {

        ReportParameter reportParameter = new ReportParameter(expanded, delta, cache, proximity_limit, network_size);
        LOGGER.log(Level.INFO, "Received request for id " +  idLogTarget + " with parameters: " + reportParameter);
        //var report = Synthetization.getReport(idLogTarget, reportParameter);
        var link = new Linking();
        link.link(idLogTarget, reportParameter);

        return Uni.createFrom().item(null);
    }
}
