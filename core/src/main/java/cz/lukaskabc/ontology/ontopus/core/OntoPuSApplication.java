package cz.lukaskabc.ontology.ontopus.core;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import java.lang.ref.Cleaner;
import java.util.ServiceLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties
public class OntoPuSApplication {
    public static final Cleaner CLEANER = Cleaner.create();

    /**
     * Adds the {@link PluginRegistryApplicationInitializer} to the application context.
     *
     * @param app Spring application to add the initializer to.
     */
    private static void addPluginRegistryInitializer(SpringApplication app) {
        final ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
        app.addInitializers(new PluginRegistryApplicationInitializer(loader));
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(OntoPuSApplication.class);
        addPluginRegistryInitializer(app);
        app.run(args);
    }
}
