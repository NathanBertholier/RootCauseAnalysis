package fr.uge.modules.api.endpoint.token;

import fr.uge.modules.api.model.TokenRequest;
import fr.uge.modules.tokenization.TokenRetriever;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static fr.uge.modules.api.serializer.TokenResponseSerializer.*;

@Path("/tokens")
public class TokenEndpoint {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TokensResponse> getTokens(TokenRequest tokenRequest){
        return TokenRetriever.getTokens(tokenRequest).map(TokensResponse::new);
    }
}
