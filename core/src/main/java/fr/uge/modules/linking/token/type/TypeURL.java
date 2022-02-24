package fr.uge.modules.linking.token.type;

import fr.uge.modules.api.model.entities.TokenEntity;

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
        return 0;
    }
}
