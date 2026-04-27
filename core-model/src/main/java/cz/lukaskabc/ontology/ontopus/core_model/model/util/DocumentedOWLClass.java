package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import java.lang.annotation.*;

/**
 * Specifies that the class is an entity class and maps to an ontological class (RDFS or OWL).
 *
 * @see cz.cvut.kbss.jopa.model.annotations.OWLClass
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DocumentedOWLClass {
    /**
     * IRI of the ontological class
     *
     * @return IRI of the referenced class
     */
    String iri();
}
