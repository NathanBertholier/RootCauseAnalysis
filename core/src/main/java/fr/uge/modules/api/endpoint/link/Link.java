package fr.uge.modules.api.endpoint.link;

import fr.uge.modules.api.EnvRetriever;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.linking.LinksResponse;
import fr.uge.modules.linking.LogsLinking;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/link")
public class Link {
    @Inject EnvRetriever envRetriever;

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<LinksResponse> getProximity(
            @QueryParam("id1") @NotNull long id_log_first,
            @QueryParam("id2") @NotNull long id_log_second,
            @QueryParam("delta") Long delta){
        if(delta == null) delta = envRetriever.reportDefaultDelta();

        final var finalDelta = delta;
        return LogEntity.<LogEntity>findById(id_log_first)
                .chain(log1 -> LogEntity.<LogEntity>findById(id_log_second)
                        .chain(log2 -> LogsLinking.computeLinks(log1, log2, finalDelta)));
    }
}
