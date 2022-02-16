package fr.uge.modules.api.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity
@Table(name = "token", schema = "public", catalog = "rootcause")
public class Token extends PanacheEntityBase {
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    public long id;
    @JsonIgnore
    @Basic
    @Column(name = "idlog")
    public long idlog;
    @OneToOne
    @JoinColumn(name = "idtokentype",insertable = false, updatable = false)
    public TokenTypeEntity token_type;
    @JsonIgnore
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

    public String[] getValue() {
        return new String[]{value};
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TokenTypeEntity getToken_type() {
        return token_type;
    }

    public void setToken_type(TokenTypeEntity token_type) {
        this.token_type = token_type;
    }
}
