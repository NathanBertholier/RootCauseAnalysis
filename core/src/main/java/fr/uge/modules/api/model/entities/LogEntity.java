package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "log", schema = "public", catalog = "rootcause")
public class LogEntity extends PanacheEntityBase {
    @Id
    @Column(name = "id")
    public long id;

    @Basic
    @Column(name = "datetime")
    public Timestamp datetime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "idlog")
    public List<TokenEntity> tokens;

    @OneToOne
    @JoinColumn(name = "id")
    public RawLog rawLog;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTokens(List<TokenEntity> tokens) {
        this.tokens = tokens;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public List<TokenEntity> getTokens() {
        return this.tokens;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public RawLog getRawLog() {
        return rawLog;
    }

    public void setRawLog(RawLog rawLog) {
        this.rawLog = rawLog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEntity logEntity = (LogEntity) o;
        return id == logEntity.id && Objects.equals(datetime, logEntity.datetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, datetime);
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
    public static Uni<List<Log>>retrieveLogs(long logId, Timestamp start, Timestamp end, int rows){
        return Log
                .find("datetime between ?1 and ?2", start, end)
                .range(0, rows).list();
    }

}