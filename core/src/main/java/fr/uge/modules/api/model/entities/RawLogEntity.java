package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.*;

@Entity
@Table(name = "rawlog")
public class RawLogEntity extends PanacheEntity {
    public String log;
}