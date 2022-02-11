package fr.uge.modules.api.server.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Log {
    @Id
    private Long id;
    private Timestamp datetime;

    @OneToMany
    private ArrayList<TokenModel> tokens;

    public Long getId() {
        return id;
    }

    public List<TokenModel> getTokens() {
        return tokens;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public void setId(long id) {
        this.id = id;
    }
}
