package cz.lukaskabc.ontology.ontopus.tests.config;

import cz.cvut.kbss.jsonld.jackson.JsonLdModule;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class TestJsonConfig {

    public static JsonMapper jsonLdMapper() {
        final JsonLdModule jsonLdModule = new JsonLdModule();
        return JsonMapper.builder().addModule(jsonLdModule).build();
    }
}
