package cz.lukaskabc.ontology.ontopus.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.core.model.LocalizationProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    private final ObjectMapper objectMapper;

    /** @param plugins Plugins to load. */
    public PluginRegistryApplicationInitializer(Iterable<Plugin> plugins) {
        this.plugins = plugins;
        this.objectMapper = new ObjectMapper();
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
                map.put(key, jsonNode.asText());
            }
        }
    }

    @Override
    public void initialize(@NonNull AnnotationConfigServletWebServerApplicationContext applicationContext) {
        LOG.info("Initializing plugins");

        Set<String> packagesForJopaScan = new HashSet<>();
        Map<String, Map<String, String>> localization = new HashMap<>();

        int pluginCount = 0;
        for (Plugin plugin : plugins) {
            LOG.info("Loading plugin: {}", plugin.getClass().getName());
            packagesForJopaScan.addAll(plugin.getJopaScanPackages());
            loadPluginLocalization(localization, plugin.getTranslations());
            plugin.getSpringScanPackages().forEach(applicationContext::scan);
            pluginCount++;
        }

        if (packagesForJopaScan.isEmpty()) {
            throw new IllegalStateException("No packages for JOPA entity scan found!");
        }

        applicationContext
                .getBeanFactory()
                .registerSingleton(
                        JopaEntityPackagesHolder.BEAN_NAME, new JopaEntityPackagesHolder(packagesForJopaScan));
        applicationContext
                .getBeanFactory()
                .registerSingleton("localizationProvider", new LocalizationProvider(localization));

        LOG.info("Loaded {} plugins", pluginCount);
    }

    private void loadPluginLocalization(
            Map<String, Map<String, String>> globalLocalization, Map<String, InputStream> pluginLocalization) {
        pluginLocalization.forEach((lang, inputStream) -> {
            final Map<String, String> language = globalLocalization.computeIfAbsent(lang, k -> new HashMap<>());
            try {
                final JsonNode jsonNode = objectMapper.readTree(inputStream);
                flattenTranslations(jsonNode, "", language);
            } catch (IOException e) {
                throw new RuntimeException("Exception while loading plugin localization:", e); // TODO
            }
        });
    }
}
