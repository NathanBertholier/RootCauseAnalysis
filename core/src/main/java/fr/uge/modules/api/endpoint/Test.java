package fr.uge.modules.api.endpoint;

import fr.uge.modules.api.model.ReportResponse;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.report.ReportParameter;
import fr.uge.modules.linking.ReportLinking;
import fr.uge.modules.synthetization.Synthetization;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.SortedMap;

@Path("/test")
public class Test {
    @GET
    public Uni<ReportResponse> test(){
        return Synthetization.getReport(221, new ReportParameter(true, 10000000, true, 1F, 10));
    }
}
