package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity
@Table(name = "token")
public class TokenModel extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public long id;

    @ManyToOne
    public Log idLog;

    @Column(name = "idtokentype")
    public Integer idTokenType;

    public String value;

    public void setValue(String value) {
        this.value = value;
    }

    public void setIdLog(Log idLog) {
        this.idLog = idLog;
    }

    public void setIdTokenType(Integer idTokenType) {
        this.idTokenType = idTokenType;
    }
}
