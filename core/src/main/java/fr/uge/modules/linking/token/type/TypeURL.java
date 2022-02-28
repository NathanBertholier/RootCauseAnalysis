package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.api.model.linking.Computation;
import fr.uge.modules.api.model.linking.Link;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TypeURL implements TokenType{

    private static final String NAME = "url";
    private static final String REGEX = "https?://www.[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&\\/\\/=]*)";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getRegex() { return REGEX; }

    @Override
    public Integer getTokenTypeId() {
        return TokenTypeId.ID_URL.getId();
    }

    @Override
    public Link computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        if(tokenLeft.isEmpty() || tokenRight.isEmpty()) {
            return Link.emptyLink(50);
        }

        String regex = "^(http[s]?://www\\.|http[s]?://|www\\.)";

        var computations = tokenLeft.stream().map(token -> token.getValue().replaceFirst(regex, ""))
                .map(tokenL -> tokenRight.stream()
                    .map(token -> token.getValue().replaceFirst(regex, "") )
                    .map(tokenR -> {
                        if (tokenL.equals(tokenR)) {
                            return new Computation(this, tokenL, tokenR, 100d);
                        }

                        var arrayL = tokenL.split("/");
                        var arrayR = tokenR.split("/");
                        double count = 0;
                        for (int i = 0; i < arrayL.length; i++) {
                            if (arrayL[i].equals(arrayR[i])) {
                                count++;
                            } else {
                                break;
                            }
                        }

                        return new Computation(this, tokenL, tokenR, (count / arrayL.length) * 100);
                    }).toList()
                ).flatMap(Collection::stream).toList();
        return new Link(computations, computations.stream().mapToDouble(Computation::proximity).sum() / computations.size());
    }
}
