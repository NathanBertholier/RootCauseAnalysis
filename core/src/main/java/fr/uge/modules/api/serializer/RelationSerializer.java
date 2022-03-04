package fr.uge.modules.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.uge.modules.api.model.linking.Relation;
import fr.uge.modules.api.model.linking.TokensLink;

import java.io.IOException;

public class RelationSerializer extends StdSerializer<Relation> {
    public RelationSerializer(){
        this(null);
    }

    public RelationSerializer(Class<Relation> clazz){
        super(clazz);
    }

    @Override
    public void serialize(Relation relation, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", relation.target().id);
        jsonGenerator.writeNumberField("proximity", relation.tokensLink().getProximity());
        jsonGenerator.writeEndObject();
    }

    /*
     @Override
    public void serialize(Relation relation, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", relation.target().id);
        jsonGenerator.writeNumberField("relations", relation.tokensLink().getProximity());
        jsonGenerator.writeEndObject();
    }

     */
}
