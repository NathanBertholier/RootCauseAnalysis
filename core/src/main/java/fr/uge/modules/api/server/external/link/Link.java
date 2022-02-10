package fr.uge.modules.api.server.external.link;

import fr.uge.modules.api.server.external.model.Computation;
import fr.uge.modules.api.server.external.model.LinksResponse;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.annotations.Query;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/link")
public class Link {
    private static final Computation computation = new Computation("type", "value1", "value2", 1);
    private static final LinksResponse response = new LinksResponse(new Computation[]{computation}, 1.05F);

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<LinksResponse> getProximity(@QueryParam("id1") long id_log_first, @QueryParam("id2") long id_log_second, @QueryParam("delta") long delta){
        return Uni.createFrom().item(response);
    }
}
