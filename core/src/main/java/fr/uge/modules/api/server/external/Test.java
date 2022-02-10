package fr.uge.modules.api.server.external;

import fr.uge.modules.api.server.external.model.Rawlog;
import fr.uge.modules.api.server.external.model.ReportParameter;
import fr.uge.modules.data.token.Token;
import fr.uge.modules.data.token.type.TokenType;
import fr.uge.modules.linking.synthetization.Linking;
import fr.uge.modules.linking.synthetization.Synthetization;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.RowSet;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/test/{id}")
public class Test {
    @Inject
    PgPool client;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Token>> getTokens(@PathParam("id") long id){
            var results = fetchTokens(id);
            System.out.println("Results linking: " + results);
            return results;
    }

    private Uni<List<Token>> fetchTokens(long id) {
        var tokens = client
                .preparedQuery("SELECT * FROM token LEFT JOIN tokenType ON token.idtokentype = tokentype.id WHERE token.id = '" + id + "'")
                .mapping(row -> new Token(row.getString("value"), TokenType.fromTokenTypeId(row.getInteger("idtokentype"))))
                .execute()
                .onItem().transformToMulti(Multi.createFrom()::iterable)
                .collect().asList();
        System.out.println("Tokens: " + tokens);
        return tokens;
    }
}
