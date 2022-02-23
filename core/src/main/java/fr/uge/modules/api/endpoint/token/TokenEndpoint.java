package fr.uge.modules.api.endpoint.token;

import fr.uge.modules.api.model.CompleteLog;
import fr.uge.modules.api.model.TokensResponse;
import fr.uge.modules.api.model.TokenRequest;
import fr.uge.modules.tokenization.TokenRetriever;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Path("/tokens")
public class TokenEndpoint {
    private static final Logger LOGGER = Logger.getLogger(TokenEndpoint.class.getName());

    @POST
    // Uni<List<CL>> instead of Uni<TokenResponse> for JSON output
    public Uni<List<CompleteLog>> getTokens(TokenRequest tokenRequest){
        return TokenRetriever.fromLogs(TokenRetriever.getTokens(tokenRequest))
                .map(TokensResponse::logDemonstrators);
    }
}
