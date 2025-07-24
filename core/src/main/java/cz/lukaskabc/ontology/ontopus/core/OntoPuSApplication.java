package cz.lukaskabc.ontology.ontopus.core;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.core.util.PluginRegistryApplicationInitializer;
import java.util.ServiceLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties
public class OntoPuSApplication {

    /**
     * Adds an {@link PluginRegistryApplicationInitializer} to the application context.
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
