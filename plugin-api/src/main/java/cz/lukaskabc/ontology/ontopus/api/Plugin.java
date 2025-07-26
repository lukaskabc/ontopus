package cz.lukaskabc.ontology.ontopus.api;

import java.util.List;
import org.jspecify.annotations.NullMarked;

/**
 * Discoverable plugin service for OntoPuS server.
 *
 * @implSpec Implementations must:
 *     <ul>
 *       <li>have a public no-arg constructor
 *       <li>be registered in {@code META-INF/services/cz.lukaskabc.ontology.ontopus.api.Plugin}
 *     </ul>
 */
@NullMarked
public interface Plugin {
    /**
     * List of base packages used for component scanning by Spring.
     *
     * @return list of base packages
     */
    default List<String> getBasePackages() {
        String thisPackage = this.getClass().getPackage().getName();
        return List.of(thisPackage);
    }

    /**
     * Unique name of the plugin used for identification.
     *
     * @return the name of the plugin
     */
    String getName();
}
