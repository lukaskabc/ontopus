package cz.lukaskabc.ontology.ontopus.core.config;

import static cz.cvut.kbss.jopa.model.JOPAPersistenceProperties.*;

import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProvider;
import cz.cvut.kbss.ontodriver.config.OntoDriverProperties;
import cz.cvut.kbss.ontodriver.rdf4j.config.Rdf4jOntoDriverProperties;
import cz.lukaskabc.ontology.ontopus.core.PluginRegistryApplicationInitializer;
import cz.lukaskabc.ontology.ontopus.core.util.JopaEntityPackagesHolder;
import cz.lukaskabc.ontology.ontopus.generated.Vocabulary;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PersistenceFactoryConfig {

    private EntityManagerFactory factory;
    private final ServerConfig serverConfig;
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public PersistenceFactoryConfig(ServerConfig serverConfig, DefaultListableBeanFactory defaultListableBeanFactory) {
        this.serverConfig = serverConfig;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @PreDestroy
    private void close() {
        if (factory.isOpen()) {
            factory.close();
        }
    }

    @Bean
    @Primary
    public EntityManagerFactory entityManagerFactory() {
        return factory;
    }

    /**
     * Packages are collected in {@link PluginRegistryApplicationInitializer PluginRegistryApplicationInitializer} and
     * passed to Spring context as {@link JopaEntityPackagesHolder}. The {@link JopaEntityPackagesHolder} bean is
     * removed from the context after the packages are retrieved.
     *
     * @return set of packages to scan for JOPA entities
     */
    private Set<String> getPackagesForEntityScan() {
        final JopaEntityPackagesHolder holder = defaultListableBeanFactory.getBean(JopaEntityPackagesHolder.class);
        defaultListableBeanFactory.destroySingleton(JopaEntityPackagesHolder.BEAN_NAME); // not needed anymore
        return holder.packagesToScan();
    }

    @PostConstruct
    private void init() {
        final ServerConfig.Database dbConfig = serverConfig.getDatabase();
        final Map<String, String> properties = new HashMap<>();

        final String packagesToScan = String.join(",", getPackagesForEntityScan());

        properties.put(SCAN_PACKAGE, packagesToScan);
        properties.put(JPA_PERSISTENCE_PROVIDER, JOPAPersistenceProvider.class.getName());

        properties.put(ONTOLOGY_URI_KEY, Vocabulary.ONTOLOGY_IRI_ONTOPUS);
        properties.put(ONTOLOGY_PHYSICAL_URI_KEY, dbConfig.getUrl());
        properties.put(DATA_SOURCE_CLASS, dbConfig.getDriver());
        properties.put(LANG, dbConfig.getLanguage());
        properties.put(PREFER_MULTILINGUAL_STRING, Boolean.TRUE.toString());

        if (dbConfig.getUsername() != null) {
            properties.put(OntoDriverProperties.DATA_SOURCE_USERNAME, dbConfig.getUsername());
            properties.put(OntoDriverProperties.DATA_SOURCE_PASSWORD, dbConfig.getPassword());
        }
        // OPTIMIZATION: Always use statement retrieval with unbound property. Should
        // spare repository queries
        properties.put(Rdf4jOntoDriverProperties.LOAD_ALL_THRESHOLD, "1");
        properties.put(JOPAPersistenceProperties.LRU_CACHE_CAPACITY, "32768");
        this.factory = Persistence.createEntityManagerFactory("ontopusPersistenceUnit", properties);
    }
}
