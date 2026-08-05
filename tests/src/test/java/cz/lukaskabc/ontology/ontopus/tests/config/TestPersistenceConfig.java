package cz.lukaskabc.ontology.ontopus.tests.config;

import cz.lukaskabc.ontology.ontopus.core.config.PersistenceConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import({TestPersistenceFactoryConfig.class, PersistenceConfig.class})
@ComponentScan
@EnableTransactionManagement
@SpringBootConfiguration
public class TestPersistenceConfig {}
