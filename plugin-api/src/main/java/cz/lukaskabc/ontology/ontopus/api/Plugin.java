package cz.lukaskabc.ontology.ontopus.api;

import java.util.List;

/**
 * Discoverable plugin service for OntoPuS server.
 *
 * @implSpec Implementations must:
 *     <ul>
 *       <li>have a public no-arg constructor
 *       <li>be registered in {@code META-INF/services/cz.lukaskabc.ontology.ontopus.api.Plugin} file
 *     </ul>
 */
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
}
