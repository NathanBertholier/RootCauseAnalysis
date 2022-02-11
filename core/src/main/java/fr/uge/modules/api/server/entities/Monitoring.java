package fr.uge.modules.api.server.entities;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Monitoring {
    @Id
    private Timestamp datetime;

    private long deliver;

    private long publish;

    private float avg_rate;

    public void setAvg_rate(float avg_rate) {
        this.avg_rate = avg_rate;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public void setDeliver(long deliver) {
        this.deliver = deliver;
    }

    public void setPublish(long publish) {
        this.publish = publish;
    }
}
