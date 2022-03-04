package fr.uge.modules.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.uge.modules.api.model.linking.Relation;
import fr.uge.modules.api.model.report.ReportResponseExpanded;

import java.io.IOException;

public class ExpendedReportSerializer extends StdSerializer<ReportResponseExpanded> {
    public ExpendedReportSerializer(){
        this(null);
    }

    public ExpendedReportSerializer(Class<ReportResponseExpanded> report){
        super(report);
    }

    @Override
    public void serialize(ReportResponseExpanded reportResponseExpanded, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        serializerProvider.defaultSerializeField("report", reportResponseExpanded.reportResponseBase(), jsonGenerator);
        jsonGenerator.writeArrayFieldStart("proximity");
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", reportResponseExpanded.relations().stream().findFirst().orElseThrow().source().id);
        serializerProvider.defaultSerializeField("links", reportResponseExpanded.relations().stream().toList(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }
}
