package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class Log extends PanacheEntityBase {
    @Id
    public Long id;
    public Timestamp datetime;

    @OneToMany
    public List<TokenModel> tokens;

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
