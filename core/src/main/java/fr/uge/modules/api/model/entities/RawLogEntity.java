package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;


/**
 * A RawLogEntity represents one unfiltered and unprocessed log in the data model. It extends the Panache entity system of the Hibernate ORM
 */
@Entity
@Table(name = "rawlog")
public class RawLogEntity extends PanacheEntity {
    @NotBlank(message = "Log content cannot be blank")
    public String log;

    public String getLog() {
        return log;
    }

    @Override
    public String toString() {
        return "RawLogEntity{" +
                "rootCause='" + log + '\'' +
                '}';
    }
}