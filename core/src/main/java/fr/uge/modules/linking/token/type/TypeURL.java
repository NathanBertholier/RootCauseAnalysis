package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

import java.util.Arrays;
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
    public double computeProximity(List<TokenEntity> tokenLeft, List<TokenEntity> tokenRight) {
        if(tokenLeft.isEmpty() || tokenRight.isEmpty()) {
            return 50;
        }

        String regex = "^(http[s]?://www\\.|http[s]?://|www\\.)";

        return tokenLeft.stream().map(token -> token.getValue().replaceFirst(regex, ""))
                .mapToDouble(tokenL -> tokenRight.stream()
                .map(token -> token.getValue().replaceFirst(regex, "") )
                .mapToDouble(tokenR -> {
            if ( tokenL.equals( tokenR ) ) {
                return 100;
            }

            var arrayL = tokenL.split( "/" );
            var arrayR = tokenR.split( "/" );
            double count = 0;
            for ( int i =0; i < arrayL.length; i++ ) {
                if ( arrayL[i].equals( arrayR[i] ) ) {
                    count++;
                }
                else {
                    break;
                }
            }
            return (count / arrayL.length) * 100;
        } ).reduce( 0,Double::max)).reduce(0, Double::sum) / tokenLeft.size();
    }
}
