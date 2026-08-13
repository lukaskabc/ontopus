package cz.lukaskabc.ontology.ontopus.tests.config;

import cz.lukaskabc.ontology.ontopus.core.config.PersistenceConfig;
import cz.lukaskabc.ontology.ontopus.core.util.ClockProvider;
import cz.lukaskabc.ontology.ontopus.core.util.JopaEntityPackagesHolder;
import cz.lukaskabc.ontology.ontopus.core_model.CoreModel;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Set;

@Import({TestPersistenceFactoryConfig.class, PersistenceConfig.class, TestSecurityConfig.class})
@EnableTransactionManagement
@ImportAutoConfiguration(ValidationAutoConfiguration.class)
@ComponentScan(basePackageClasses = {CoreModel.class})
public class TestPersistenceConfig {
    @Bean
    ClockProvider clockProvider() {
        return new ClockProvider();
    }

    @Bean
    public JopaEntityPackagesHolder jopaEntityPackagesHolder() {
        return new JopaEntityPackagesHolder(Set.of("cz.lukaskabc.ontology.ontopus"));
    }
}
