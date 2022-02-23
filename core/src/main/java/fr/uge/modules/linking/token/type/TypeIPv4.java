package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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

    private static double jagguard(String t1, String t2) {
        var toto = t1.split("\\.");
        var titi = t2.split("\\.");
        System.out.println(titi.length);
        if(toto[0].equals(titi[1])) {

        } else if(toto[0].equals(titi[1])) {

        }
            Arrays.stream(titi).forEach(System.out::println);
        return IntStream.range(0, 4).filter(i -> !toto[i].equals(titi[i]))
                .mapToDouble(i -> switch (i) {
                    case 1 -> 20;
                    case 2 -> 85;
                    case 3 -> 95;
                    default -> 0;
                }).findFirst().orElse(100);
    }

    public float computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        if(tokenLeft.isEmpty() || tokenRight.isEmpty()) {
            return 50;
        }
        tokenRight.forEach(System.out::println);
        return (float) tokenLeft.stream()
                .map(TokenEntity::getValue)
                .mapToDouble(leftIP -> tokenRight.stream()
                        .map(TokenEntity::getValue)
                        .mapToDouble(rightIP -> jagguard(leftIP, rightIP))
                        .max().orElse(0)).max().orElseThrow();
    }

}
