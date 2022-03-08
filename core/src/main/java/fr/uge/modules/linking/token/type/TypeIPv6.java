package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.TokensLink;
import fr.uge.modules.linking.strategy.AverageStrategy;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * TypeIPv6 TokenType
 * The current REGEX matches on compressed IPv6.
 * If you want to only match uncompressed IPv6 use the other REGEX
 */
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

    /**
     * Method inherited from TokenType interface
     * Compute the proximity between each token of the same time for two logs
     * @param listTokensLeft Tokens from log 1
     * @param listTokensRight Tokens from log 2
     * @return a TokensLink object containing all the computations
     */
    @Override
    public TokensLink computeProximity(List<TokenEntity> listTokensLeft, List<TokenEntity> listTokensRight) {
        if (listTokensLeft.isEmpty() || listTokensRight.isEmpty()) return TokensLink.withoutStrategy(0);
        var type = new TypeIPv6();

        var computations = listTokensLeft.stream().map(tokenL -> listTokensRight.stream()
                .map(tokenR -> {
                    if(tokenR.equals(tokenL))
                        return new Computation(type, tokenL.value, tokenR.value, 100d);
                    return new Computation(type, tokenL.value, tokenR.value, 0d);
                })
                .toList())
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingDouble(computation -> - computation.proximity()))
                .limit(Integer.max(listTokensLeft.size(), listTokensRight.size()))
                .toList();

        return new TokensLink(computations, new AverageStrategy());
    }

}
