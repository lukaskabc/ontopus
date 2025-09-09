package cz.lukaskabc.ontology.ontopus.core.model.utils;

import java.lang.annotation.*;

/**
 * Specifies that the class is an entity class and maps to an ontological class (RDFS or OWL).
 *
 * <p>The annotation has no effect and serves only documentation purpose.
 *
 * @see cz.cvut.kbss.jopa.model.annotations.OWLClass
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface DocumentedOWLClass {
    /**
     * IRI of the ontological class
     *
     * @return IRI of the referenced class
     */
    String iri();
}
