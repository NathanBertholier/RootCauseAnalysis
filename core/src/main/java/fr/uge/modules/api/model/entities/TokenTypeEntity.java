package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tokentype", schema = "public", catalog = "rootcause")
public class TokenTypeEntity extends PanacheEntity {
    public String name;
}
