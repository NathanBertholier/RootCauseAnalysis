package fr.uge.modules.api.server.external.report;

import fr.uge.modules.api.server.external.model.ReportParameter;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/report/{id}")
public class Report {
    private static final Logger LOGGER = Logger.getLogger(Report.class.getName());

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<String> getReport(@PathParam("id") int idLogTarget, ReportParameter reportParameter){
        LOGGER.log(Level.INFO, "Received request for id " +  idLogTarget + " with parameters: " + reportParameter);
        return Uni.createFrom().item("Rapport");
    }
}
