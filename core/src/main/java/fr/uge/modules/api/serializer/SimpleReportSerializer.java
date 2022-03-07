package fr.uge.modules.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.uge.modules.api.model.report.GenericReport;

import java.io.IOException;

public class SimpleReportSerializer extends StdSerializer<GenericReport> {
    public SimpleReportSerializer(){
        this(null);
    }

    public SimpleReportSerializer(Class<GenericReport> report){
        super(report);
    }

    @Override
    public void serialize(GenericReport genericReport, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        serializerProvider.defaultSerializeField("rootCause", genericReport.getRoot(), jsonGenerator);
        serializerProvider.defaultSerializeField("target", genericReport.getTarget(), jsonGenerator);
        serializerProvider.defaultSerializeField("tokens", genericReport.getSeenTokens().stream().toList(), jsonGenerator);
        serializerProvider.defaultSerializeField("logs", genericReport.getRelevantLogs(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }
}