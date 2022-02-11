package fr.uge.db.insert.tokens;

import fr.uge.db.insert.log.LogInserter;
import fr.uge.modules.api.server.entities.Log;
import fr.uge.modules.api.server.entities.Token;
import fr.uge.modules.api.server.external.model.TokenModel;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class LogTokens {
    private static final Logger LOGGER = Logger.getGlobal();

    @Inject
    EntityManager em;

    public void insertTokens(long id, Date date, List<TokenModel> tokens) {
        Log log = new Log();
        log.setId(id);
        log.setDatetime(Timestamp.from(Instant.now()));

        tokens.forEach(token -> {
            Token to = new Token();
            to.setIdLog(log);
            to.setValue(token.token_value());
            to.setIdtokentype(switch (token.token_type()) {
                case "IPV4" -> 1;
                case "IPV6" -> 2;
                case "Statut" -> 3;
                case "Datetime" -> 4;
                case "EdgeResponse" -> 5;
                default -> 1;
            });
            em.persist(to);
        });
        em.persist(log);
    }
}
