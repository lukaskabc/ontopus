package cz.lukaskabc.ontology.ontopus.core_model.model.converter;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.SerializableImportProcessContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;

@SuppressWarnings("unused") // used by jopa by @Converter auto apply annotation
@Converter(autoApply = true)
public class SerializableImportProcessContextConverter
        implements AttributeConverter<SerializableImportProcessContext, String> {

    private static final Logger log = LogManager.getLogger(SerializableImportProcessContextConverter.class);
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
            throw log.throwing(
                    InternalException.serializationException("Error during import process context deserialization", e));
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
            throw log.throwing(
                    InternalException.serializationException("Error during import process context serialization", e));
        }
    }
}
