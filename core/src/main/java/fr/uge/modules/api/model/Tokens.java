package fr.uge.modules.api.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import java.sql.Timestamp;
import java.util.List;

public class Tokens extends PanacheEntity {
    public long typeId;
    public Timestamp timestamp;
    public List<TokenModel> tokens;

    public static void insertTokens(){

    }
}
