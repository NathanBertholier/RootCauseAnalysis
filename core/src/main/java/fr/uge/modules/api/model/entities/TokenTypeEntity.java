package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tokentype", schema = "public", catalog = "rootcause")
public class TokenTypeEntity extends PanacheEntity {
    public String name;
}
