package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Monitoring extends PanacheEntityBase {
    @Id
    @Column(name = "datetime")
    private Timestamp datetime;
    @Basic
    @Column(name = "deliver")
    private long deliver;
    @Basic
    @Column(name = "publish")
    private long publish;
    @Basic
    @Column(name = "avg_rate")
    private double avgRate;

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public long getDeliver() {
        return deliver;
    }

    public void setDeliver(long deliver) {
        this.deliver = deliver;
    }

    public long getPublish() {
        return publish;
    }

    public void setPublish(long publish) {
        this.publish = publish;
    }

    public double getAvgRate() {
        return avgRate;
    }

    public void setAvgRate(double avgRate) {
        this.avgRate = avgRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Monitoring that = (Monitoring) o;
        return deliver == that.deliver && publish == that.publish && Double.compare(that.avgRate, avgRate) == 0 && Objects.equals(datetime, that.datetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datetime, deliver, publish, avgRate);
    }
}
