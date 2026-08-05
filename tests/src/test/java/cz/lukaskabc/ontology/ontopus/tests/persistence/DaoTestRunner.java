package cz.lukaskabc.ontology.ontopus.tests.persistence;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.tests.config.TestPersistenceConfig;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest
@EnableConfigurationProperties(OntopusConfig.class)
@ContextConfiguration(
        classes = {TestPersistenceConfig.class},
        initializers = {ConfigDataApplicationContextInitializer.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public abstract class DaoTestRunner {}
