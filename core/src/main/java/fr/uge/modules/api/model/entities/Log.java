package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
//@Table(name = "log", schema = "public", catalog = "rootcause")
public class Log extends PanacheEntityBase {
    @Id
    @Column(name = "id")
    public long id;

    @Basic
    @Column(name = "datetime")
    public Timestamp datetime;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "idlog")
    public List<Token> tokens;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public List<Token> getTokens() {
        return this.tokens;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", datetime=" + datetime +
                ", tokens=" + tokens +
                '}';
    }

    // Partiel pour le moment
    public static Uni<List<Log>> retrieveLogs(long logId, Timestamp start, Timestamp end, int rows){
        return Log
                .find("id = ?1 and datetime between ?2 and ?3", logId, start, end)
                .range(0, rows).list();
    }

}
