package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.List;

public interface TokenType {

    String getName();
    String getRegex();
    Integer getTokenTypeId();

    int matcher(String word);

    float computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight);

    class TokenTypeId{
        public static final int ID_IPV4 = 1;
        public static final int ID_IPV6 = 2;
        public static final int ID_STATUS = 3;
        public static final int ID_DATETIME = 4;
        public static final int ID_EDGERESPONSE = 5;

    }

}
