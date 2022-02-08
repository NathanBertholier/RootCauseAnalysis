package fr.uge.modules.api.server.external.report;

import fr.uge.modules.api.server.external.model.ReportParameter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/report/{id}")
public class Report {

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public void getReport(@PathParam("id") int idLogTarget, ReportParameter reportParameter){
        System.out.println(reportParameter);
    }
}
