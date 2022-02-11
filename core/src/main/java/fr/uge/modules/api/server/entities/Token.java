package fr.uge.modules.api.server.entities;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;

@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Log idLog;

    private Integer idtokentype;

    private String value;

    public void setValue(String value) {
        this.value = value;
    }

    public void setIdLog(Log idLog) {
        this.idLog = idLog;
    }

    public void setIdtokentype(Integer idtokentype) {
        this.idtokentype = idtokentype;
    }
}
