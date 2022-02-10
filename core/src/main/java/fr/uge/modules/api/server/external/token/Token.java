package fr.uge.modules.api.server.external.token;

import fr.uge.modules.api.server.external.model.LogDemonstrator;
import fr.uge.modules.api.server.external.model.TokenModel;
import fr.uge.modules.api.server.external.model.TokenRequest;
import fr.uge.modules.api.server.external.model.TokensResponse;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/tokens")
public class Token {
    private static final TokenModel TOKEN_MODEL = new TokenModel("IP", "10.16.27.62.244");
    private static final TokenModel[] TOKEN_MODELS = new TokenModel[]{TOKEN_MODEL};
    private static final LogDemonstrator logDemonstrator =  new LogDemonstrator(1, "2021-11-20T00:00:05.000", "2021-11-20 00:00:01 10.16.27.62.244 GET", TOKEN_MODELS);
    private final LogDemonstrator[] logDemonstrators = new LogDemonstrator[]{ logDemonstrator };

    @POST
    public Uni<List<TokensResponse>> getTokens(TokenRequest tokenRequest){
        return Uni.createFrom().item(List.of(new TokensResponse(logDemonstrators)));
    }
}
