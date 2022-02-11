package fr.uge.modules.api.endpoint.token;

import fr.uge.modules.api.model.TokensResponse;
import fr.uge.modules.api.model.TokenRequest;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Collections;
import java.util.List;

@Path("/tokens")
public class TokenEndpoint {
    //private static final TokenModel TOKEN_MODEL = new TokenModel(new TypeIPv4(), "10.16.27.62.244");
    //private static final TokenModel[] TOKEN_MODELS = new TokenModel[]{TOKEN_MODEL};
    //private static final LogDemonstrator logDemonstrator =  new LogDemonstrator(1, "2021-11-20T00:00:05.000", "2021-11-20 00:00:01 10.16.27.62.244 GET", TOKEN_MODELS);
    //private final LogDemonstrator[] logDemonstrators = new LogDemonstrator[]{ };

    @POST
    public Uni<List<TokensResponse>> getTokens(TokenRequest tokenRequest){
        return Uni.createFrom().item(Collections.emptyList());
    }
}
