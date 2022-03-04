package fr.uge.modules.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.uge.modules.api.model.linking.TokensLink;

import java.io.IOException;

public class TokensLinkSerializer extends StdSerializer<TokensLink> {
    public TokensLinkSerializer(){
        this(null);
    }

    public TokensLinkSerializer(Class<TokensLink> clazz){
        super(clazz);
    }

    @Override
    public void serialize(TokensLink tokensLink, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumber(1);
        jsonGenerator.writeEndObject();
    }
}
