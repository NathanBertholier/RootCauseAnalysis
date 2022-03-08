package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * A MonitoringEntity represents information about the current API load and status in the data model.
 * It extends the Panache entity system of the 'Hibernate ORM'
 */
@Entity
@Table(name = "monitoring")
public class MonitoringEntity extends PanacheEntityBase {
    @Id
    @Column(name = "datetime")
    private Timestamp datetime;
    @Basic
    @Column(name = "deliver")
    private double deliver;
    @Basic
    @Column(name = "publish")
    private double publish;
    @Basic
    @Column(name = "messages")
    private double messages;

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public double getDeliver() {
        return deliver;
    }

    public void setDeliver(double deliver) {
        this.deliver = deliver;
    }

    public double getPublish() {
        return publish;
    }

    public void setPublish(double publish) {
        this.publish = publish;
    }

    public double getMessages() {
        return messages;
    }

    public void setMessages(double messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitoringEntity that = (MonitoringEntity) o;
        return Double.compare(that.deliver, deliver) == 0 && Double.compare(that.publish, publish) == 0 && Double.compare(that.messages, messages) == 0 && Objects.equals(datetime, that.datetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datetime, deliver, publish, messages);
    }

    @Override
    public String toString() {
        return "MonitoringEntity{" +
                "datetime=" + datetime +
                ", deliver=" + deliver +
                ", publish=" + publish +
                ", messages=" + messages +
                '}';
    }
}
