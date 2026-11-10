package cz.lukaskabc.ontology.ontopus.tests.integration;

import cz.lukaskabc.ontology.ontopus.core.OntoPuSApplication;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.tests.config.TestPersistenceFactoryConfig;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Starts full spring application with volatile storage. Use
 * {@link cz.lukaskabc.ontology.ontopus.tests.util.LoadPlugins @LoadPlugins} to load additional plugins, by default only
 * {@link cz.lukaskabc.ontology.ontopus.tests.util.LoadPlugins#REQUIRED_PLUGINS required plugins} are loaded.
 */
@Execution(ExecutionMode.CONCURRENT)
@EnableConfigurationProperties(OntopusConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(classes = {OntoPuSApplication.class, TestPersistenceFactoryConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseRestIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected OntopusConfig config;
}
