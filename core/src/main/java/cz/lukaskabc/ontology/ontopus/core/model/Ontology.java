package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.vocabulary.OWL;
import cz.lukaskabc.ontology.ontopus.generated.Vocabulary;
import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@OWLClass(iri = Ontology.Meta.s_TYPE)
public class Ontology extends PersistenceEntity {

    @OWLDataProperty(iri = Meta.s_VERSION_INFO)
    private String versionInfo;

    @OWLDataProperty(iri = Meta.s_VERSION_IRI)
    private URI versionIri;

    public static class Meta {
        public static final String s_TYPE = OWL.ONTOLOGY;
        public static final URI TYPE = URI.create(s_TYPE);
        public static final String s_VERSION_INFO = Vocabulary.s_p_org_versionInfo;
        public static final URI VERSION_INFO = URI.create(s_VERSION_INFO);
        public static final String s_VERSION_IRI = Vocabulary.s_p_versionIRI;
        public static final URI VERSION_IRI = URI.create(s_VERSION_IRI);

        private Meta() {
            throw new AssertionError();
        }
    }
}
