package fr.uge.modules.api.endpoint.token;

import fr.uge.modules.api.model.TokenRequest;
import fr.uge.modules.error.NotYetTokenizedError;
import fr.uge.modules.tokenization.TokenRetriever;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import static fr.uge.modules.api.serializer.TokenResponseSerializer.*;
import static fr.uge.modules.error.AbstractRootCauseError.fromError;

@Path("/tokens")
public class TokenEndpoint {
    private static final Logger LOGGER = Logger.getLogger(TokenEndpoint.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getTokens(TokenRequest tokenRequest) {
        return TokenRetriever.getTokens(tokenRequest)
                .map(TokensResponse::new)
                .map(tokensResponse -> Response.ok(tokensResponse).build())
                .onFailure(NotYetTokenizedError.class).recoverWithItem(fromError(new NotYetTokenizedError()))
                .onFailure().invoke(
                        error -> LOGGER.severe(() -> "Error while retrieving tokens: " + error)
                );
    }
}
