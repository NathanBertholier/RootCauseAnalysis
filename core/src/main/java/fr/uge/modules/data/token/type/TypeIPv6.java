package fr.uge.modules.data.token.type;

import fr.uge.modules.data.token.Token;

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
    public float computeProximity(Token t1, Token t2) {
        return jaccard(t1.getValue(), t2.getValue());
    }

}