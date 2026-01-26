package cz.lukaskabc.ontology.ontopus.core_model.model.converter;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.SerializableImportProcessContext;
import java.util.Base64;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Converter(autoApply = true)
public class SerializableImportProcessContextConverter
        implements AttributeConverter<SerializableImportProcessContext, String> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Base64.Decoder decoder = Base64.getDecoder();

    @Override
    public @Nullable SerializableImportProcessContext convertToAttribute(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        try {
            return mapper.readValue(decoder.decode(s), SerializableImportProcessContext.class);
        } catch (JacksonException e) {
            throw new RuntimeException(e); // TODO exception
        }
    }

    @Nullable @Override
    public String convertToAxiomValue(@Nullable SerializableImportProcessContext serializableImportProcessContext) {
        if (serializableImportProcessContext == null) {
            return null;
        }
        try {
            return encoder.encodeToString(mapper.writeValueAsBytes(serializableImportProcessContext));
        } catch (JacksonException e) {
            throw new RuntimeException(e); // TODO exception
        }
    }
}
