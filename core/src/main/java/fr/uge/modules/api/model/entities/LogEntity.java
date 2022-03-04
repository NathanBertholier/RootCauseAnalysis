package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "log", schema = "public", catalog = "rootcause")
@NamedQuery(name="LogEntity.findAllWithJoin", query = "SELECT l FROM LogEntity l" +
        " LEFT JOIN FETCH l.tokens item  WHERE l.id <> ?1 and l.datetime between ?2 and ?3")

@JsonSerialize(using = LogSerializer.class)
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
    public RawLogEntity rawLog;

    @Column(name = "autogeneratedDatetime")
    public boolean autogeneratedDatetime;

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

    public RawLogEntity getRawLog() {
        return rawLog;
    }

    public void setRawLog(RawLogEntity rawLog) {
        this.rawLog = rawLog;
    }

    public boolean getAutogeneratedDatetime() {
        return autogeneratedDatetime;
    }

    public void setAutogeneratedDatetime(boolean autogeneratedDatetime) {
        this.autogeneratedDatetime = autogeneratedDatetime;
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
    public static Uni<List<LogEntity>> findAllWithJoin(Long id, Timestamp datetime1, Timestamp datetime2){
        return LogEntity.<LogEntity>find("#LogEntity.findAllWithJoin",id,datetime1,datetime2).list();
    }

}
