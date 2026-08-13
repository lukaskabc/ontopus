package cz.lukaskabc.ontology.ontopus.core.rest.serialization;

import cz.cvut.kbss.jopa.model.MultilingualString;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class MultilingualStringDeserializer extends ValueDeserializer<MultilingualString> {
    static final String NO_LANGUAGE_KEY = "none";

    @Nullable @Override
    public MultilingualString deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        if (p.currentToken() != JsonToken.START_OBJECT) {
            ctxt.reportWrongTokenException(
                    this, JsonToken.START_OBJECT, "Expected a JSON object representing a MultilingualString.");
            return null;
        }

        final MultilingualString result = new MultilingualString();
        String key;
        while ((key = p.nextName()) != null) {
            p.nextToken(); // advance to the value token
            final String value = p.getString();
            final String language = NO_LANGUAGE_KEY.equals(key) ? null : key;
            result.set(language, value);
        }

        return result;
    }
}
