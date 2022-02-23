package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "monitoring")
public class MonitoringEntity extends PanacheEntityBase {
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
        MonitoringEntity that = (MonitoringEntity) o;
        return deliver == that.deliver && publish == that.publish && Double.compare(that.avgRate, avgRate) == 0 && Objects.equals(datetime, that.datetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datetime, deliver, publish, avgRate);
    }

    @Override
    public String toString() {
        return "MonitoringEntity{" +
                "datetime=" + datetime +
                ", deliver=" + deliver +
                ", publish=" + publish +
                ", avgRate=" + avgRate +
                '}';
    }
}
