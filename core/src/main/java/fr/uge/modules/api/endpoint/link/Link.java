package fr.uge.modules.api.endpoint.link;

import fr.uge.modules.api.EnvRetriever;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.linking.TokensLink;
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
    public Uni<TokensLink> getProximity(
            @QueryParam("id1") @NotNull long idLogFirst,
            @QueryParam("id2") @NotNull long idLogSecond,
            @QueryParam("delta") Long delta){
        if(delta == null) delta = envRetriever.reportDefaultDelta();

        final var finalDelta = delta;
        return LogEntity.<LogEntity>findById(idLogFirst)
                .chain(log1 -> LogEntity.<LogEntity>findById(idLogSecond)
                        .chain(log2 -> LogsLinking.computeLinks(log1, log2, finalDelta))
                        );
    }
}
