package fr.uge.modules.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.uge.modules.api.model.linking.Computation;

import java.io.IOException;

public class ComputationSerializer extends StdSerializer<Computation> {
    public ComputationSerializer(){
        this(null);
    }

    public ComputationSerializer(Class<Computation> clazz){
        super(clazz);
    }

    @Override
    public void serialize(Computation computation, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("token_type", computation.token_type().getName());
        jsonGenerator.writeStringField("value_log1", computation.value_log_first());
        jsonGenerator.writeStringField("value_log2", computation.value_log_second());
        jsonGenerator.writeNumberField("proximity", computation.proximity());
        jsonGenerator.writeEndObject();
    }
}
