package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.List;

public class TypeIPv6 implements TokenType{

    private static final String NAME = "ipv6";
    //format sans compression : "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"
    private static final String REGEX = "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))";

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
    public float computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        if ( tokenLeft.isEmpty() || tokenRight.isEmpty() ) return 0;

        float proximity = 0;
        for (TokenEntity tokenL : tokenLeft) {
            for ( TokenEntity tokenR : tokenRight ) {
                if ( tokenL.getValue().equals( tokenR.getValue() ) ) {
                    proximity += 100;
                    break;
                }
            }
        }

        return proximity / tokenLeft.size();
    }

}
