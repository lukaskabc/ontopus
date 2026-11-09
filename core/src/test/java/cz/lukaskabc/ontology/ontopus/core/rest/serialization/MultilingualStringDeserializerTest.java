package cz.lukaskabc.ontology.ontopus.core.rest.serialization;

import static org.junit.jupiter.api.Assertions.*;

import cz.cvut.kbss.jopa.model.MultilingualString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

class MultilingualStringDeserializerTest {

    private ObjectMapper mapper;

    @Test
    void deserializesEmptyObject() {
        final String json = "{}";

        final MultilingualString result = mapper.readValue(json, MultilingualString.class);

        assertTrue(result.isEmpty());
    }

    @Test
    void deserializesMultipleLanguageValues() {
        final String json = """
				{"en":"Hello","cs":"Ahoj"}
				""";

        final MultilingualString result = mapper.readValue(json, MultilingualString.class);

        assertEquals("Hello", result.get("en"));
        assertEquals("Ahoj", result.get("cs"));
        assertEquals(2, result.size());
    }

    @Test
    @Disabled // TODO: test multilingual string behavior after null was replaced with @none
    void deserializesNoneKeyAsNullLanguage() {
        final String json = """
				{"@none":"no language tag"}
				""";

        final MultilingualString result = mapper.readValue(json, MultilingualString.class);

        assertTrue(result.containsKey(null));
        assertEquals("no language tag", result.get(null));
    }

    @Test
    void roundTripsThroughSerializerAndDeserializer() {
        SimpleModule module = new SimpleModule();
        final ObjectMapper roundTripMapper =
                JsonMapper.builder().addModule(module).build();

        final MultilingualString original = new MultilingualString();
        original.set("en", "Hello");
        original.set("cs", "Ahoj");
        original.set(null, "Fallback");

        final String json = roundTripMapper.writeValueAsString(original);
        final MultilingualString result = roundTripMapper.readValue(json, MultilingualString.class);

        assertEquals("Hello", result.get("en"));
        assertEquals("Ahoj", result.get("cs"));
        assertEquals("Fallback", result.get(null));
    }

    @BeforeEach
    void setUp() {
        SimpleModule module = new SimpleModule();
        mapper = JsonMapper.builder().addModule(module).build();
    }

    @Test
    void throwsOnArrayToken() {
        final String json = """
				["en", "Hello"]
				""";

        assertThrows(JacksonException.class, () -> mapper.readValue(json, MultilingualString.class));
    }

    @Test
    void throwsOnNonObjectToken() {
        final String json = """
				"just a string"
				""";

        assertThrows(JacksonException.class, () -> mapper.readValue(json, MultilingualString.class));
    }
}
