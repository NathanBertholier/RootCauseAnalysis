package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.TokenModel;

public class TypeIPv6 implements TokenType{

    private final String name = "ipv6";
    //format sans compression : "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"
    private final String regex = "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRegex() { return regex; }

    @Override
    public Integer getTokenTypeId() {
        return TokenTypeId.ID_IPV6;
    }


    public static float cardBetween(String t1, String t2){
        int res = 0;
        for(int i = 0; i < Math.min(t1.length(),t2.length()); i++){
            if(t1.charAt(i) == t2.charAt(i))
                res++;
        }
        return res;
    }

    public static float jaccard(String t1, String t2){
        return (cardBetween(t1, t2) / t1.length()) * 100;
    }

    @Override
    public float computeProximity(TokenModel t1, TokenModel t2) {
        return jaccard(t1.token_value(), t2.token_value());
    }

}
