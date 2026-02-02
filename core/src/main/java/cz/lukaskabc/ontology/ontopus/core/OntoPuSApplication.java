package cz.lukaskabc.ontology.ontopus.core;

import cz.lukaskabc.ontology.ontopus.core_model.CoreModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.ref.Cleaner;

@EnableAsync
@SpringBootApplication(scanBasePackageClasses = {OntoPuSApplication.class, CoreModel.class})
@ConfigurationPropertiesScan(basePackageClasses = {OntoPuSApplication.class, CoreModel.class})
@EnableConfigurationProperties
public class OntoPuSApplication {
    public static final Cleaner CLEANER = Cleaner.create();

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(OntoPuSApplication.class);
        app.run(args);
    }
}
