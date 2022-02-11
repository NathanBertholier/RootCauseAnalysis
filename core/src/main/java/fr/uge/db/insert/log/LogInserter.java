package fr.uge.db.insert.log;

import fr.uge.modules.api.server.entities.RawLog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.logging.Logger;

@ApplicationScoped
public class LogInserter {
    private static final Logger LOGGER = Logger.getGlobal();

    @Inject
    EntityManager em;

    public void insert(long id, String value) {
        RawLog rawLog = new RawLog();
        rawLog.setValue(value);
        rawLog.setId(id);

        em.persist(rawLog);
    }
}
