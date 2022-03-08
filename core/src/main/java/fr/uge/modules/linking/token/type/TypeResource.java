package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.TokensLink;
import fr.uge.modules.linking.strategy.AverageStrategy;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public class TypeResource implements TokenType{

    private static final String NAME = "resource";
    private static final String REGEX = "^(/(\\S)+)+";

    @Override
    public String getName() { return NAME; }

    @Override
    public String getRegex() { return REGEX; }

    @Override
    public Integer getTokenTypeId() { return TokenTypeId.ID_RESOURCE.getId(); }

    @Override
    public TokensLink computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        if(tokenLeft.isEmpty() || tokenRight.isEmpty()) {
            return TokensLink.withoutStrategy(50);
        }
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2);
        format.setRoundingMode(RoundingMode.FLOOR);
        var computations = tokenLeft.stream().map(TokenEntity::getValue)
                .map(tokenL -> tokenRight.stream()
                        .map(TokenEntity::getValue)
                        .map(tokenR -> {
                            if ( tokenL.equals( tokenR ) ) {
                                return new Computation(this, tokenL, tokenR, 100d);
                            }
                            var startsWith = "/";
                            var arrayL = tokenL.split(startsWith);
                            var arrayR = tokenR.split(startsWith);
                            double count = IntStream.range(1, arrayL.length - 1)
                                    .filter(i -> arrayR.length > i)
                                    .filter(i -> !arrayR[i].equals(arrayL[i]))
                                    .findFirst()
                                    .orElse(0);
                            var res = Double.parseDouble(format.format(((count - 1) / Math.min(arrayL.length, arrayR.length)) * 100).replace(",", "."));
                            return new Computation(this, tokenL, tokenR, res);
                        } )
                        .toList())
                .flatMap(Collection::stream)
                .toList();
        return new TokensLink(computations, new AverageStrategy());
    }

}
