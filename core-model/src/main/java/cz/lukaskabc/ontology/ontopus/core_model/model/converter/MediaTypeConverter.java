package cz.lukaskabc.ontology.ontopus.core_model.model.converter;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;

@Converter(autoApply = true)
public class MediaTypeConverter implements AttributeConverter<MediaType, String> {
    @Override
    public @Nullable MediaType convertToAttribute(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return MediaType.parseMediaType(value);
    }

    @Override
    public @Nullable String convertToAxiomValue(@Nullable MediaType value) {
        if (value != null) {
            return value.toString();
        }
        return null;
    }
}
