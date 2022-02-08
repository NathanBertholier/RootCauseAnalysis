package fr.uge.modules.api.server.external.link;

import fr.uge.modules.api.server.external.model.Computation;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/link")
public class Link {
    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<Computation> getProximity(@QueryParam("id1") int id_log_first, @QueryParam("id2") int id_log_second){
        return Uni.createFrom().item(new Computation("type", "value1", "value2", 1));
    }
}
