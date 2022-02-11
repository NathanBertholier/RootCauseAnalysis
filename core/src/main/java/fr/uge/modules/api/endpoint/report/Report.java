package fr.uge.modules.api.endpoint.report;

import fr.uge.modules.api.model.ReportResponse;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.synthetization.Synthetization;
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
    public Uni<ReportResponse> getReport(@PathParam("id") long idLogTarget, ReportParameter reportParameter) throws SQLException {
        LOGGER.log(Level.INFO, "Received request for id " +  idLogTarget + " with parameters: " + reportParameter);
        var report = Synthetization.getReport(idLogTarget, reportParameter);
        return Uni.createFrom().item(report);
    }
}
