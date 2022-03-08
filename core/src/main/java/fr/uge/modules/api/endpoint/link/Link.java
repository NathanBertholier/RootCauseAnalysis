package fr.uge.modules.api.endpoint.link;

import fr.uge.modules.api.EnvRetriever;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.error.NotYetTokenizedError;
import fr.uge.modules.linking.LogsLinking;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.logging.Logger;

import static fr.uge.modules.error.AbstractRootCauseError.fromError;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Endpoint used to fetch logs computations. This endpoint accepts two logs, contained in the DB.
 * Returns an error if one of the requested logs does not exist inside the DB.
 * Otherwise, returns the computation between the specific logs.
 */
@Path("/link")
public class Link {
    private static final Logger LOGGER = Logger.getLogger(Link.class.getName());

    // Variable retriever. These variables refer to the global environment variables, contained in the .env file
    @Inject EnvRetriever envRetriever;

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<Response> getProximity(
            @QueryParam("id1") @NotNull long idLogFirst,
            @QueryParam("id2") @NotNull long idLogSecond,
            @QueryParam("delta") Long delta){
        if(delta == null) delta = envRetriever.reportDefaultDelta();

        // Needed in order to perform lambda computation
        final var finalDelta = delta;

        return LogEntity.<LogEntity>findById(idLogFirst)
                .chain(log1 -> LogEntity.<LogEntity>findById(idLogSecond)
                        .chain(log2 -> LogsLinking.computeLinks(log1, log2, finalDelta))
                        )
                .map(tokensLink -> Response.ok(tokensLink).build())
                // Returns an error if one of the requested logs does not exist inside the DB
                .onFailure(NotYetTokenizedError.class).recoverWithItem(() -> fromError(new NotYetTokenizedError()))
                .onFailure().invoke(error ->
                        LOGGER.severe(() -> "Error while calculating link between " + idLogFirst + " and " + idLogSecond + ": " + error)
                );
    }
}
