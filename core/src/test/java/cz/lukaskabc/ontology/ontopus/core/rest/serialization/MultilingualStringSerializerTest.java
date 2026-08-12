package cz.lukaskabc.ontology.ontopus.core.rest.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cz.cvut.kbss.jopa.model.MultilingualString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

class MultilingualStringSerializerTest {

    private ObjectMapper mapper;

    @Test
    void serializesEmptyMultilingualStringAsEmptyObject() {
        final MultilingualString value = new MultilingualString();

        final String json = mapper.writeValueAsString(value);
        final JsonNode node = mapper.readTree(json);

        assertTrue(node.isObject());
        assertEquals(0, node.size());
    }

    @Test
    void serializesMixedLanguageAndNoLanguageEntries() {
        final MultilingualString value = new MultilingualString();
        value.set("en", "Hello");
        value.set(null, "Fallback");

        final String json = mapper.writeValueAsString(value);
        final JsonNode node = mapper.readTree(json);

        assertEquals("Hello", node.get("en").asString());
        assertEquals(
                "Fallback",
                node.get(MultilingualStringDeserializer.NO_LANGUAGE_KEY).asString());
        assertEquals(2, node.size());
    }

    @Test
    void serializesMultipleLanguageValues() {
        final MultilingualString value = new MultilingualString();
        value.set("en", "Hello");
        value.set("cs", "Ahoj");

        final String json = mapper.writeValueAsString(value);
        final JsonNode node = mapper.readTree(json);

        assertTrue(node.isObject());
        assertEquals("Hello", node.get("en").asString());
        assertEquals("Ahoj", node.get("cs").asString());
    }

    @Test
    void serializesNullLanguageAsNoneKey() {
        final MultilingualString value = new MultilingualString();
        value.set(null, "no language tag");

        final String json = mapper.writeValueAsString(value);
        final JsonNode node = mapper.readTree(json);

        assertTrue(node.has(MultilingualStringDeserializer.NO_LANGUAGE_KEY));
        assertEquals(
                "no language tag",
                node.get(MultilingualStringDeserializer.NO_LANGUAGE_KEY).asString());
    }

    @BeforeEach
    void setUp() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(MultilingualString.class, new MultilingualStringSerializer());
        mapper = JsonMapper.builder().addModule(module).build();
    }

    @Test
    void writesJsonNullForNullValue() {
        final MultilingualString value = null;
        final String json = mapper.writeValueAsString(value);
        assertEquals("null", json);
    }
}
