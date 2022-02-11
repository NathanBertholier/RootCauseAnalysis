package fr.uge.modules.api.server.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Log {
    @Id
    private Long id;
    private Timestamp datetime;

    public Long getId() {
        return id;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp from) {
    }

    public void setId(long id) {
    }
}
