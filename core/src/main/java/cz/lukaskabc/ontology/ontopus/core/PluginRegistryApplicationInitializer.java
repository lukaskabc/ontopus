package cz.lukaskabc.ontology.ontopus.core;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.core.service.LocalizationProvider;
import cz.lukaskabc.ontology.ontopus.core.util.Constants;
import cz.lukaskabc.ontology.ontopus.core.util.JopaEntityPackagesHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Scans each plugin's base package for Spring components and registers them in the application context. Main plugin
 * classes are also registered in the application context under their names.
 */
@Component
public class PluginRegistryApplicationInitializer implements BeanDefinitionRegistryPostProcessor {
    private static final Logger LOG = LogManager.getLogger(PluginRegistryApplicationInitializer.class);

    private static void scanForSpringBeans(
            Collection<String> basePackages, BeanDefinitionRegistry beanDefinitionRegistry) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
        scanner.addExcludeFilter(new AnnotationTypeFilter(SpringBootApplication.class));
        scanner.scan(basePackages.toArray(new String[0]));
    }

    private final Set<String> packagesForSpringScan = new HashSet<>();
    private final Set<String> packagesForJopaScan = new HashSet<>();

    private final Map<String, Map<String, String>> localization = new HashMap<>();

    private final Iterable<Plugin> plugins;

    private final ObjectMapper objectMapper;

    public PluginRegistryApplicationInitializer() {
        this.plugins = ServiceLoader.load(Plugin.class);
        this.objectMapper = new ObjectMapper();

        LOG.info("Discovering plugins");
        loadGlobalLocalizations(localization);

        int pluginCount = 0;
        for (Plugin plugin : plugins) {
            LOG.info("Loading plugin: {}", plugin.getClass().getName());
            packagesForJopaScan.addAll(plugin.getJopaScanPackages());
            loadPluginLocalization(localization, plugin.getTranslations());
            packagesForSpringScan.addAll(plugin.getSpringScanPackages());
            pluginCount++;
        }

        LOG.info("Found {} plugins", pluginCount);
    }

    /**
     * Flattens nested keys in translation JSON.
     *
     * <p>Input: <code>
     * <pre>
     *  {
     *      "first": {
     *          "second": {
     *              "third": "value"
     *              "next": "another value"
     *          }
     *      }
     *  }
     * </pre>
     * </code>
     *
     * <p>Output: <code>
     * <pre>
     * {
     *     "first.second.third": "value"
     *     "first.second.next": "another value"
     * }
     * </pre>
     * </code>
     *
     * @param jsonNode Json object to flatten.
     * @param prefix Key prefix for the current nested level.
     * @param map The map to fill.
     * @throws IllegalStateException when a duplicate key is found
     */
    private void flattenTranslations(JsonNode jsonNode, String prefix, Map<String, String> map) {
        if (jsonNode.isObject()) {
            Set<Map.Entry<String, JsonNode>> properties = jsonNode.properties();
            for (Map.Entry<String, JsonNode> property : properties) {
                flattenTranslations(property.getValue(), prefix + property.getKey() + ".", map);
            }
        } else if (jsonNode.isValueNode()) {
            // remove leading dot
            final String key = prefix.substring(0, prefix.length() - 1);
            if (map.containsKey(key)) {
                throw new IllegalStateException("Duplicate translation key: " + key);
            } else {
                map.put(key, jsonNode.asString());
            }
        }
    }

    /**
     * Loads localization resources from {@code /language/} directory. Only {@link Constants#LANGUAGES} are attempted to
     * load.
     *
     * @param localization the global localization map of languages to translation keys.
     */
    private void loadGlobalLocalizations(Map<String, Map<String, String>> localization) {
        try {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            for (String lang : Constants.LANGUAGES) {
                final Enumeration<URL> files = cl.getResources("language/" + lang + ".json");
                while (files.hasMoreElements()) {
                    final URL url = files.nextElement();
                    final Map<String, String> language = localization.computeIfAbsent(lang, k -> new HashMap<>());
                    flattenTranslations(objectMapper.readTree(url.openStream()), "", language);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO
        }
    }

    private void loadPluginLocalization(
            Map<String, Map<String, String>> globalLocalization, Map<String, InputStream> pluginLocalization) {
        pluginLocalization.forEach((lang, inputStream) -> {
            final Map<String, String> language = globalLocalization.computeIfAbsent(lang, k -> new HashMap<>());
            try {
                final JsonNode jsonNode = objectMapper.readTree(inputStream);
                flattenTranslations(jsonNode, "", language);
            } catch (JacksonException e) {
                throw new RuntimeException("Exception while loading plugin localization:", e); // TODO
            }
        });
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        if (!packagesForSpringScan.isEmpty()) {
            scanForSpringBeans(packagesForSpringScan, registry);
        }
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinitionRegistryPostProcessor.super.postProcessBeanFactory(beanFactory);
        if (packagesForJopaScan.isEmpty()) {
            throw new IllegalStateException("No packages for JOPA entity scan found!");
        }
        beanFactory.registerSingleton(
                JopaEntityPackagesHolder.BEAN_NAME, new JopaEntityPackagesHolder(packagesForJopaScan));
        beanFactory.registerSingleton("localizationProvider", new LocalizationProvider(localization));
    }
}
