package cz.lukaskabc.ontology.ontopus.tests.integration;

import cz.lukaskabc.ontology.ontopus.core.config.SystemConfig;
import cz.lukaskabc.ontology.ontopus.tests.config.TestJsonConfig;
import cz.lukaskabc.ontology.ontopus.tests.persistence.BaseDaoTest;
import org.springframework.context.annotation.Import;

@Import({TestJsonConfig.class, SystemConfig.class})
public class BaseIntegrationTest extends BaseDaoTest {}
