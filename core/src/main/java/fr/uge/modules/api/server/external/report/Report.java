package fr.uge.modules.api.server.external.report;

import fr.uge.modules.api.server.external.model.*;
import fr.uge.modules.linking.synthetization.Synthetization;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.*;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/report/{id}")
public class Report {
    private static final Logger LOGGER = Logger.getLogger(Report.class.getName());

    private static final Log log = new Log(1L, "2021-11-20 00:00:01 10.16.27.62.244 GET index.html", "2021-12-29T21:54:05.000");
    private static final TokenModel[] TOKEN_MODELS = new TokenModel[]{ new TokenModel("IP", "10.0.0.0")};
    private static final Log[] logs = new Log[]{ log };

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<ReportResponse> getReport(@PathParam("id") long idLogTarget, ReportParameter reportParameter) throws SQLException {
        LOGGER.log(Level.INFO, "Received request for id " +  idLogTarget + " with parameters: " + reportParameter);
        var report = Synthetization.getReport(idLogTarget, reportParameter);
        return Uni.createFrom().item(new ReportResponse(log, TOKEN_MODELS, logs));
    }
}
