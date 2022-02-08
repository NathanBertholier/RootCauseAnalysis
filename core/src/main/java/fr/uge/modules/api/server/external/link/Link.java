package fr.uge.modules.api.server.external.link;

import fr.uge.modules.api.server.external.model.Computation;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/link")
public class Link {
    private static final Computation computation = new Computation("type", "value1", "value2", 1);

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<Computation> getProximity(@QueryParam("id1") long id_log_first, @QueryParam("id2") long id_log_second){
        return Uni.createFrom().item(computation);
    }
}
