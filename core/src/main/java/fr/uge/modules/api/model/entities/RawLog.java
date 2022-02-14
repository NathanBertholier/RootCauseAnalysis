package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity(name = "rawlog")
public class RawLog extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long id;
    public String log;

    @Override
    public String toString() {
        return "RawLog{" +
                "value='" + log + '\'' +
                '}';
    }

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
}