package fr.uge.modules.api.endpoint.token;

import fr.uge.modules.api.model.TokenRequest;
import fr.uge.modules.tokenization.TokenRetriever;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.uge.modules.api.serializer.TokenResponseSerializer.*;

@Path("/tokens")
public class TokenEndpoint {
    private static final Logger LOGGER = Logger.getLogger(TokenEndpoint.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getTokens(TokenRequest tokenRequest) {
        return testDateTime(tokenRequest.init_datetime(), tokenRequest.end_datetime())
                .orElse(TokenRetriever.getTokens(tokenRequest)
                .map(logs -> new TokensResponse(logs, ""))
                .map(tokensResponse -> Response.ok(tokensResponse).build())
                .onFailure()
                .recoverWithItem(() -> {
                    if(tokenRequest.id() == -1) {
                        return Response.ok(new TokensResponse(new ArrayList<>(), "No data found.")).build();
                    }
                    return Response.ok(new TokensResponse(new ArrayList<>(), "Log no found with id : " + tokenRequest.id())).build();
                })
                .onFailure().invoke(
                        error -> LOGGER.severe(() -> "Error while retrieving tokens: " + error)
                ));
    }

    private Optional<Uni<Response>> testDateTime(String initDatetime, String endDatetime) {
        if(!initDatetime.isEmpty() && !endDatetime.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:m:s");
                if(!sdf.parse(initDatetime).before(sdf.parse(endDatetime))) {
                    return Optional.of(Uni.createFrom()
                            .item(Response.ok(new TokensResponse(new ArrayList<>(), "The start date cannot be greater than the end date."))
                                    .build()));
                }
            } catch (ParseException e) {
                LOGGER.log(Level.SEVERE, "Error while parsing date in tokenEndPoint", e);
            }
        }
        return Optional.empty();
    }
}
