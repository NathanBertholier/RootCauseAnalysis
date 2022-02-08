package fr.uge.modules.api.server.external.link;

import fr.uge.modules.api.server.external.model.Computation;
import fr.uge.modules.api.server.external.model.LinksResponse;
import io.smallrye.mutiny.Uni;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/link")
public class Link {
    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<LinksResponse> getProximity(@NotNull @QueryParam("id1") int id_log_first, @NotNull @QueryParam("id2") int id_log_second, @QueryParam("delta") int delta){
        return Uni.createFrom().item(new LinksResponse(
                new Computation[]{new Computation("type", "value1", "value2", 1)}, 1.97F));
    }
}
