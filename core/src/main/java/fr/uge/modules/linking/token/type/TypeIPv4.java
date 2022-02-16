package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.TokenModel;

public class TypeIPv4 implements TokenType {

    private static final String NAME = "ipv4";
    private static final String REGEX = "(([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])\\.){3}([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getRegex() { return REGEX; }

    @Override
    public Integer getTokenTypeId() {
        return TokenTypeId.ID_IPV4.getId();
    }

    private static float cardBetween(String t1, String t2){
        int res = 0;
        for(int i = 0; i < Math.min(t1.length(),t2.length()); i++){
            if(t1.charAt(i) == t2.charAt(i))
                res++;
        }
        return res;
    }

    private static float jaccard(String t1, String t2){
        return (cardBetween(t1, t2) / t1.length()) * 100;
    }

    public float computeProximity(TokenModel t1, TokenModel t2) {
        return jaccard(t1.token_value(), t2.token_value());
    }

}
