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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "idlog")
    public List<TokenEntity> tokens;

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

    public static Uni<List<LogEntity>> retrieveLogs(long logId, Timestamp start, Timestamp end, int rows){
        return LogEntity
                .find("id = ?1 and datetime between ?2 and ?3", logId, start, end)
                .range(0, rows).list();
    }
}
