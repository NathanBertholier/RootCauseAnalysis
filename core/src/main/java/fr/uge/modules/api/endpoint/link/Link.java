package fr.uge.modules.api.endpoint.link;

import fr.uge.modules.api.EnvRetriever;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.api.model.linking.TokensLink;
import fr.uge.modules.error.AbstractRootCauseError;
import fr.uge.modules.error.NotYetTokenizedError;
import fr.uge.modules.linking.LogsLinking;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.logging.Logger;

import static fr.uge.modules.error.AbstractRootCauseError.fromError;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/link")
public class Link {
    private static final Logger LOGGER = Logger.getLogger(Link.class.getName());
    @Inject EnvRetriever envRetriever;

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<Response> getProximity(
            @QueryParam("id1") @NotNull long id_log_first,
            @QueryParam("id2") @NotNull long id_log_second,
            @QueryParam("delta") Long delta){
        if(delta == null) delta = envRetriever.reportDefaultDelta();

        final var finalDelta = delta;
        return LogEntity.<LogEntity>findById(id_log_first)
                .chain(log1 -> LogEntity.<LogEntity>findById(id_log_second)
                        .chain(log2 -> LogsLinking.computeLinks(log1, log2, finalDelta))
                        )
                .map(tokensLink -> Response.ok(tokensLink).build())
                .onFailure(NotYetTokenizedError.class).recoverWithItem(() -> fromError(new NotYetTokenizedError()))
                .onFailure().invoke(error ->
                        LOGGER.severe(() -> "Error while calculating link between " + id_log_first + " and " + id_log_second + ": " + error)
                );
    }
}
