package fr.uge.modules.api.server.external;

import fr.uge.modules.api.server.external.model.ReportParameter;
import fr.uge.modules.data.token.Token;
import fr.uge.modules.linking.synthetization.Linking;
import fr.uge.modules.linking.synthetization.Synthetization;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/test/{id}")
public class Test {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Token>> getTokens(@PathParam("id") long id){
        try {
            var link = new Linking("", 1, new ReportParameter(true, 1L, true, 1F, 1));
            var results = link.getTokens(id);
            System.out.println("Results linking: " + results);
            return results;
        } catch (SQLException e) {
            System.out.println("Error linking: " + e);
            return Uni.createFrom().nullItem();
        }
    }
}
