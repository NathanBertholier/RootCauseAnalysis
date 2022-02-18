package fr.uge.modules.api.endpoint.link;

import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.LinksResponse;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/link")
public class Link {
    private static final Computation computation = new Computation("type", "value1", "value2", 1);
    private static final LinksResponse response = new LinksResponse(new Computation[]{computation}, 1.05F);

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<LinksResponse> getProximity(
            @QueryParam("id1") @NotNull long id_log_first,
            @QueryParam("id2") @NotNull long id_log_second,
            @QueryParam("delta") Long delta){
        if(delta == null){
            // get default value
        }
        return Uni.createFrom().item(response);
    }
}
