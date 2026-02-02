package cz.lukaskabc.ontology.ontopus.api;

import org.jspecify.annotations.NullMarked;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Discoverable plugin service for OntoPuS server.
 *
 * @implSpec Implementations must:
 *     <ul>
 *       <li>have a public no-arg constructor
 *       <li>be registered in {@code META-INF/services/cz.lukaskabc.ontology.ontopus.api.Plugin} file
 *     </ul>
 */
@NullMarked
public interface Plugin {
    /**
     * List of packages that should be scanned by JOPA for entity declarations.
     *
     * @return list of packages to scan for JOPA entities.
     */
    default List<String> getJopaScanPackages() {
        return List.of();
    }

    /**
     * List of base packages used for component scanning by Spring on system startup. By default, the base package of
     * the Plugin concrete class is used, which also includes all sub-packages.
     *
     * @return list of base packages to scan for Spring components
     */
    default List<String> getSpringScanPackages() {
        String thisPackage = this.getClass().getPackage().getName();
        return List.of(thisPackage);
    }

    /**
     * Load language translations in i18n JSON format. Every translation key must be unique for the application
     * including all plugins.
     *
     * <p>Expected JSON format: <code><pre>
     * {
     *     "some.plugin.translation.key": "translation value",
     *     "some": {
     *         "plugin": {
     *             "first": "It is also possible",
     *             "second": "to use nested keys.",
     *             "third": "Those keys will be flattened"
     *         }
     *     }
     * }
     * </pre></code>
     *
     * @return Map of language codes to stream of the translations file.
     * @implNote Note that all plugins are loaded with the same class loader so resources with matching paths will
     *     clash. If the plugin defines translations in {@code /language/} directory ({@code /language/en.json}), it
     *     will be loaded automatically by the core and an empty map should be returned here. Otherwise, a unique folder
     *     should be used for all plugin's resources (e.g. use plugin's package).
     */
    default Map<String, InputStream> getTranslations() {
        return Map.of();
    }
}
