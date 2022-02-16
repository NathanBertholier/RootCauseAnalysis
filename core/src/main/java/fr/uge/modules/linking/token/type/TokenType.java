package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.TokenModel;

import java.util.Optional;

public interface TokenType {

    String getName();
    String getRegex();
    Integer getTokenTypeId();

    default int matcher(String word){
        if(word.matches(getRegex())){
            return getTokenTypeId();
        }
        return -1;
    }

    float computeProximity(TokenModel t1, TokenModel t2);

    public class TokenTypeId{
        public static final int ID_IPV4 = 1;
        public static final int ID_IPV6 = 2;
        public static final int ID_STATUS = 3;
        public static final int ID_DATETIME = 4;
        public static final int ID_EDGERESPONSE = 5;

    }

}
