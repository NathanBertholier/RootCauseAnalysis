package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.TokenModel;

public class TypeIPv6 implements TokenType{

    private static final String NAME = "ipv6";
    //format sans compression : "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"
    private static final String REGEX = "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getRegex() { return REGEX; }

    @Override
    public Integer getTokenTypeId() {
        return TokenTypeId.ID_IPV6.getId();
    }

    @Override
    public float computeProximity(TokenModel t1, TokenModel t2) {
        if(t1.token_value().equals(t2.token_value())){
            return 100;
        }
        return 0;
    }

}
