package fr.uge.modules.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.uge.modules.api.model.entities.TokenEntity;
import fr.uge.modules.linking.token.type.TokenType;

import java.io.IOException;

public class TokenSerializer extends StdSerializer<TokenEntity> {
    public TokenSerializer(){
        this(null);
    }

    public TokenSerializer(Class<TokenEntity> clazz){
        super(clazz);
    }

    @Override
    public void serialize(TokenEntity token, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", TokenType.fromId(token.idtokentype).getName());
        jsonGenerator.writeStringField("value", token.value);
        jsonGenerator.writeEndObject();
    }
}
