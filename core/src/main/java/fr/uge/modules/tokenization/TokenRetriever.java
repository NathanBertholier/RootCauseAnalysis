package fr.uge.modules.tokenization;

import fr.uge.modules.api.model.TokenModel;
import fr.uge.modules.api.model.TokenRequest;
import fr.uge.modules.api.model.entities.LogEntity;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/*
 * Class used to get a List of LogEntity for the /tokens endpoint.
 */
public class TokenRetriever {
    private static final String AND = " and ";
    private static final Logger LOGGER = Logger.getGlobal();

    TokenRetriever() {}

    /**
     * Method used in endpoint to get the List of LogEntity depending on the tokenRequest parameter.
     * @param tokenRequest  TokenRequest that contain each parameter.
     * @return              A Uni of a List of LogEntity that represent the query result.
     */
    public static Uni<List<LogEntity>> getTokens(TokenRequest tokenRequest){
        var rows = Objects.requireNonNullElse(tokenRequest.rows(), 30) - 1;
        StringBuilder sQuery = new StringBuilder();
        sQuery.append("select l from LogEntity l where 1 = 1 ");

        var end = tokenRequest.end_datetime();
        var start = tokenRequest.init_datetime();

        if(end.isEmpty()) {
            if(!start.isEmpty()) {
                  sQuery.append(AND)
                          .append(" l.datetime > '")
                          .append(start)
                          .append("'");
            }
        } else {
            if(start.isEmpty()) {
               sQuery.append(AND)
                       .append(" l.datetime < '")
                       .append(end)
                       .append("'");
            } else {
                sQuery.append(AND)
                        .append(" datetime between '")
                        .append(start)
                        .append("' and '")
                        .append(end)
                        .append("'");
            }
        }

        var id = tokenRequest.id();
        if(id != -1) {
            return LogEntity
                    .<LogEntity>find(sQuery + " and id = ?1 ", id)
                    .range(0,rows)
                    .list()
                    .onItem()
                    .invoke(() -> LOGGER.log(Level.INFO, "Log found"))
                    .onFailure()
                    .invoke(e -> LOGGER.log(Level.SEVERE, "Error while finding ID", e));
        }
        return getTokensWithoutId(sQuery, tokenRequest.tokens(), rows);
    }

    /**
     * Method called if the request do not contain an ID.
     * @param sQuery    Actual Query that contain the time
     * @param tokens    List of TokenModel that represent each token requested.
     * @param rows      The numbers of Row wanted.
     * @return          A List of LogEntity that represent the query result.
     */
    private static Uni<List<LogEntity>> getTokensWithoutId(StringBuilder sQuery, List<TokenModel> tokens, int rows) {
        if(!tokens.isEmpty()) {
            sQuery.append(" and l.id in ( select t0.idlog from TokenEntity t0 ");
            IntStream.range(1, tokens.size() + 1).forEach(i -> sQuery.append(" join TokenEntity t")
                    .append(i)
                    .append(" on t")
                    .append(i)
                    .append(".idlog = t")
                    .append(i-1)
                    .append(".idlog and t")
                    .append(i)
                    .append(".value = '")
                    .append(tokens.get(i - 1).token_value())
                    .append("' and t")
                    .append(i)
                    .append(".idtokentype = ")
                    .append(tokens.get(i - 1).token_type()));
            sQuery.append(" ) ");
        }
        return LogEntity.<LogEntity>find(sQuery.toString())
                .range(0, rows)
                .list();
    }
}
