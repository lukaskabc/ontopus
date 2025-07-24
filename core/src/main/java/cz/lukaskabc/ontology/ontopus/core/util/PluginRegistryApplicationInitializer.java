package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContextInitializer;

/**
 * Scans each plugin's base package for Spring components and registers them in the application context. Main plugin
 * classes are also registered in the application context under their names.
 */
public class PluginRegistryApplicationInitializer
        implements ApplicationContextInitializer<AnnotationConfigServletWebServerApplicationContext> {
    private static final Logger LOG = LoggerFactory.getLogger(PluginRegistryApplicationInitializer.class);

    private final Iterable<Plugin> plugins;

    /** @param plugins Plugins to register in the application context. */
    public PluginRegistryApplicationInitializer(Iterable<Plugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    public void initialize(@NonNull AnnotationConfigServletWebServerApplicationContext applicationContext) {
        LOG.info("Initializing plugins");

        for (Plugin plugin : plugins) {
            LOG.info("Loaded plugin: {}", plugin.getName());
            plugin.getBasePackages().forEach(applicationContext::scan);
            applicationContext.getBeanFactory().registerSingleton(plugin.getName(), plugin);
        }
    }
}
