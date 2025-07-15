package cz.lukaskabc.ontology.ontopus.api;

/**
 * Discoverable plugin service for OntoPuS server.
 *
 * @implSpec Implementations must:
 * <ul>
 *  <li>have a public no-arg constructor</li>
 *  <li>be registered in {@code META-INF/services/cz.lukaskabc.ontology.ontopus.api.Plugin}</li>
 * </ul>
 * @implNote The package of the implementation class is used as the base package for component scanning.
 */
public interface Plugin {
    /**
     * Unique name of the plugin used for identification.
     *
     * @return the name of the plugin
     */
    String getName();
}
