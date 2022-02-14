package fr.uge.modules.api.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.List;

@Entity(name = "rawlog")
public class RawLog extends PanacheEntity {
    public String log;

    @Override
    public String toString() {
        return "RawLog{" +
                "value='" + log + '\'' +
                '}';
    }
}