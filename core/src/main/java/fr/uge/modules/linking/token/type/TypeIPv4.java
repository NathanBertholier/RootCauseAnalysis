package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

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

    private static double calculIp(String t1, String t2) {
        var sIP1 = t1.split("\\.");
        var sIP2 = t2.split("\\.");

        return IntStream.range(0, 4).filter(i -> !sIP1[i].equals(sIP2[i]))
                .mapToDouble(i -> switch (i) {
                    case 1 -> 20;
                    case 2 -> 85;
                    case 3 -> 95;
                    default -> 0;
                }).findFirst().orElse(100);
    }

    public double computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        if(tokenLeft.isEmpty() || tokenRight.isEmpty()) {
            return 50;
        }
        return tokenLeft.stream()
                    .map(TokenEntity::getValue)
                    .mapToDouble(leftIP -> tokenRight.stream()
                            .map(TokenEntity::getValue)
                            .mapToDouble(rightIP -> calculIp(leftIP, rightIP))
                            .reduce(0, Double::max))
                .reduce(0, Double::sum) / tokenLeft.size();
    }
}
