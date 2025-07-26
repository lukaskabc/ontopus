package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.lukaskabc.ontology.ontopus.generated.Vocabulary;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter(onMethod_ = {@Override})
@Setter
@Accessors(chain = true)
@OWLClass(iri = Ontology.Meta.s_TYPE)
public class Ontology extends PersistenceEntity implements cz.lukaskabc.ontology.ontopus.api.model.Ontology {
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Meta.s_NAME, simpleLiteral = true)
    @NotEmpty private String name;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Meta.s_ONTOLOGY_URI, simpleLiteral = true)
    @NotNull private URI ontologyUri;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Meta.s_VERSION_INFO, simpleLiteral = true)
    @NotEmpty private String versionInfo;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Meta.s_VERSION_IRI, simpleLiteral = true)
    @NotNull private URI versionIri;

    // TODO: remove MOD ontology as it is only a draft and make own
    public static class Meta {
        public static final String s_TYPE = Vocabulary.s_c_mod_SemanticArtefact;
        public static final URI TYPE = URI.create(s_TYPE);
        public static final String s_ONTOLOGY_URI = Vocabulary.s_i_mod_URI;
        public static final URI ONTOLOGY_URI = URI.create(s_ONTOLOGY_URI);
        public static final String s_NAME = Vocabulary.s_i_mod_title;
        public static final URI NAME = URI.create(s_NAME);
        public static final String s_VERSION_INFO = Vocabulary.s_i_mod_versionInfo;
        public static final URI VERSION_INFO = URI.create(s_VERSION_INFO);
        public static final String s_VERSION_IRI = Vocabulary.s_i_mod_versionIRI;
        public static final URI VERSION_IRI = URI.create(s_VERSION_IRI);

        private Meta() {
            throw new AssertionError();
        }
    }
}
