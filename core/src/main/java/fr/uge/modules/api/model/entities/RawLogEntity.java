package fr.uge.modules.api.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "rawlog")
public class RawLogEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public long id;

    @Basic
    @Column(name = "value")
    public String log;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawLogEntity that = (RawLogEntity) o;
        return id == that.id && Objects.equals(log, that.log);
    }

    @Override
    public String toString() {
        return "RawLog{" +
                "id=" + id +
                ", value='" + log + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, log);
    }
}