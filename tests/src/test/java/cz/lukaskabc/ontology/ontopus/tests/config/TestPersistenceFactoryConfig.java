package cz.lukaskabc.ontology.ontopus.tests.config;

import cz.cvut.kbss.ontodriver.rdf4j.config.Rdf4jOntoDriverProperties;
import cz.lukaskabc.ontology.ontopus.core.config.PersistenceFactoryConfig;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;

@Configuration
@Profile("test")
public class TestPersistenceFactoryConfig extends PersistenceFactoryConfig {

    public TestPersistenceFactoryConfig(
            OntopusConfig serverConfig, DefaultListableBeanFactory defaultListableBeanFactory) {
        super(serverConfig, defaultListableBeanFactory);
    }

    @Override
    protected Map<String, String> createFactoryProperties() {
        final Map<String, String> properties = super.createFactoryProperties();
        properties.put(Rdf4jOntoDriverProperties.USE_VOLATILE_STORAGE, "true");
        properties.put(Rdf4jOntoDriverProperties.USE_INFERENCE, "false");
        return properties;
    }
}
