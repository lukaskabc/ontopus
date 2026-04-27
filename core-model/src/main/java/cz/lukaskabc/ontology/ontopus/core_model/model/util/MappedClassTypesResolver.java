package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import org.springframework.core.annotation.MergedAnnotations;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

public class MappedClassTypesResolver {
    public static Set<URI> resolveTypes(Class<?> clazz) {
        return MergedAnnotations.search(MergedAnnotations.SearchStrategy.SUPERCLASS).from(clazz).stream()
                .filter(merged -> merged.getType().isAssignableFrom(DocumentedOWLClass.class)
                        || merged.getType().isAssignableFrom(OWLClass.class))
                .map(merged -> merged.getString("iri"))
                .distinct()
                .map(URI::create)
                .collect(Collectors.toUnmodifiableSet());
    }

    private MappedClassTypesResolver() {
        throw new AssertionError();
    }
}
