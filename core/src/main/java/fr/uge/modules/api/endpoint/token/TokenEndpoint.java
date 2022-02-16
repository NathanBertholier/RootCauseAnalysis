package fr.uge.modules.api.endpoint.token;

import fr.uge.modules.api.model.CompleteLog;
import fr.uge.modules.api.model.TokensResponse;
import fr.uge.modules.api.model.TokenRequest;
import fr.uge.modules.tokenization.TokenRetriever;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.logging.Logger;

@Path("/tokens")
public class TokenEndpoint {
    private static final Logger LOGGER = Logger.getLogger(TokenEndpoint.class.getName());

    @POST
    @Blocking
    public Uni<CompleteLog[]> getTokens(TokenRequest tokenRequest){
        var builder = new StringBuilder();
        builder.append("TokenRequest: ").append(tokenRequest);
        Uni<TokensResponse> responseUni = TokenRetriever.fromLogs(TokenRetriever.getTokens(tokenRequest));
        responseUni.invoke(tokensResponse -> System.out.println(tokensResponse)).await().indefinitely();
        return responseUni.map(TokensResponse::logDemonstrators);
    }


}
