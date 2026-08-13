package cz.lukaskabc.ontology.ontopus.core.rest.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.net.URI;

class TypedIdentifierSerializerTest {

    private ObjectMapper mapper;

    @Test
    void serializesUnderlyingUri() {
        final URI uri = URI.create("https://example.org/entity/123");
        final TypedIdentifier identifier = mock(TypedIdentifier.class);
        when(identifier.toURI()).thenReturn(uri);

        final String json = mapper.writeValueAsString(identifier);

        assertEquals("\"https://example.org/entity/123\"", json);
    }

    @BeforeEach
    void setUp() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(TypedIdentifier.class, new TypedIdentifierSerializer());
        mapper = JsonMapper.builder().addModule(module).build();
    }

    @Test
    void writesJsonNullForNullIdentifier() {
        final TypedIdentifier identifier = null;
        final String json = mapper.writeValueAsString(identifier);
        assertEquals("null", json);
    }
}
