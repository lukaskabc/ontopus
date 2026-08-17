package cz.lukaskabc.ontology.ontopus.tests.rest;

import cz.lukaskabc.ontology.ontopus.core.OntoPuSApplication;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Execution(ExecutionMode.CONCURRENT)
@EnableConfigurationProperties(OntopusConfig.class)
@ContextConfiguration(
        classes = {OntoPuSApplication.class},
        initializers = {ConfigDataApplicationContextInitializer.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MvcTestRunner {
    @Autowired
    protected MockMvc mockMvc;
}
