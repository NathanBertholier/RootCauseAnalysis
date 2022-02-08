package fr.uge.modules.api.server.external.token;

import fr.uge.modules.api.server.external.model.LogDemonstrator;
import fr.uge.modules.api.server.external.model.TokenRequest;
import fr.uge.modules.api.server.external.model.TokensResponse;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/tokens")
public class Token {
    @GET
    public Uni<List<TokensResponse>> getTokens(TokenRequest tokenRequest){
        return Uni.createFrom().item(List.of(new TokensResponse(new LogDemonstrator[]{
             new LogDemonstrator(
                     1,
                     "2021-11-20T00:00:05.000",
                     "2021-11-20 00:00:01 10.16.27.62.244 GET",
                     new fr.uge.modules.api.server.external.model.Token[]{
                             new fr.uge.modules.api.server.external.model.Token("IP", "10.16.27.62.244")
                     })
        })));
    }
}
