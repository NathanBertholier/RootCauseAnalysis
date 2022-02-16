package fr.uge.modules.api.model.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity
@Table(name = "token", schema = "public", catalog = "rootcause")
public class TokenEntity extends PanacheEntityBase {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    public long id;
    @Basic
    @Column(name = "idlog")
    public long idlog;
    @Basic
    @Column(name = "idtokentype")
    public int idtokentype;
    @Basic
    @Column(name = "value")
    public String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdlog() {
        return idlog;
    }

    public void setIdlog(long idlog) {
        this.idlog = idlog;
    }

    public int getIdtokentype() {
        return idtokentype;
    }

    public void setIdtokentype(int idtokentype) {
        this.idtokentype = idtokentype;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TokenEntity{" +
                "id=" + id +
                ", idlog=" + idlog +
                ", idtokentype=" + idtokentype +
                ", value='" + value + '\'' +
                '}';
    }
}
