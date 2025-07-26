package cz.lukaskabc.ontology.ontopus.core.util;

import java.util.Collections;
import java.util.Set;

/**
 * One time use object for system initialization. Once are the packages consumed, the bean should be removed from the
 * spring context.
 */
public record JopaEntityPackagesHolder(Set<String> packagesToScan) {
    public static final String BEAN_NAME = "jopaEntityPackagesHolder";

    public JopaEntityPackagesHolder(Set<String> packagesToScan) {
        this.packagesToScan = Collections.unmodifiableSet(packagesToScan);
    }
}
