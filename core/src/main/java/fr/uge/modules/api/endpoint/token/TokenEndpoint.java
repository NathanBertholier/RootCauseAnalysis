package fr.uge.modules.api.endpoint.token;

import fr.uge.modules.api.model.TokenRequest;
import fr.uge.modules.api.model.entities.LogEntity;
import fr.uge.modules.tokenization.TokenRetriever;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;

@Path("/tokens")
public class TokenEndpoint {
    private static final Logger LOGGER = Logger.getLogger(TokenEndpoint.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<LogEntity>> getTokens(TokenRequest tokenRequest){
        return TokenRetriever.getTokens(tokenRequest);
    }
}
