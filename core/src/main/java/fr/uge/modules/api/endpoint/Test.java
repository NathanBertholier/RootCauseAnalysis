package fr.uge.modules.api.endpoint;

import fr.uge.modules.api.model.report.GenericReport;
import fr.uge.modules.api.model.report.ReportResponse;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.synthetization.Synthetization;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/test")
public class Test {
    @GET
    public Uni<GenericReport> test(){
        return Synthetization.getReport(221, new ReportParameter(true, 10000000, true, 1F, 10));
    }
}
