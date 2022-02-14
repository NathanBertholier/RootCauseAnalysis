package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import javax.persistence.Entity;

@Entity
public class TokenType extends PanacheEntity {
    private String name;
}
