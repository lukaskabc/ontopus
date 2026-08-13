package cz.lukaskabc.ontology.ontopus.core;

import cz.cvut.kbss.ontodriver.rdf4j.Rdf4jDataSource;
import cz.cvut.kbss.runner.MigrationRunner;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OntologyMigrationApplicationInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Logger log = LogManager.getLogger(OntologyMigrationApplicationInitializer.class);

    private static OntopusConfig.Database bindConfig(Environment environment) {
        return Binder.get(environment).bindOrCreate("ontopus.database", OntopusConfig.Database.class);
    }

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        log.info("Preparing data migration...");
        final OntopusConfig.Database database = bindConfig(applicationContext.getEnvironment());

        if (!database.getDriver().equals(Rdf4jDataSource.class.getName())) {
            log.warn(
                    "Selected database driver may cause failure of data migration! Only RDF4J databases are supported. Selected driver: {}",
                    database.getDriver());
        }

        final MigrationRunner migrationRunner = MigrationRunner.repository(database.getUrl())
                .username(database.getUsername())
                .password(database.getPassword())
                .changelogFile("migration/root-changelog.yaml")
                .build();

        log.info("Performing data migration...");
        migrationRunner.run();
    }
}
