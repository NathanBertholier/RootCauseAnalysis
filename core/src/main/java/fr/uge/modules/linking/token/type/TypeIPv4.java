package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.Link;

import java.util.Collection;
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

    public Link computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        if(tokenLeft.isEmpty() || tokenRight.isEmpty()) return Link.emptyLink(50);
        var type = new TypeIPv4();

        var computations = tokenLeft.stream()
                    .map(TokenEntity::getValue)
                    .map(leftIP -> tokenRight.stream()
                            .map(TokenEntity::getValue)
                            .map(rightIP -> new Computation(type, leftIP, rightIP, calculIp(leftIP, rightIP)))
                            .toList()
                    ).flatMap(Collection::stream).toList();

        return new Link(computations, computations.stream().mapToDouble(Computation::proximity).sum() / computations.size());
    }
}
