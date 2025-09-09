package cz.lukaskabc.ontology.ontopus.core.model.dcat;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core.model.utils.DocumentedOWLClass;
import java.net.URI;
import java.time.Instant;
import java.util.Set;

/** Resource published or curated by a single agent. */
@MappedSuperclass
@DocumentedOWLClass(iri = Vocabulary.s_c_dcat_Resource_A)
public abstract class Resource extends PersistenceEntity {
    /*
     * skipping access rights, conforms to, contact point, creator
     */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_description)
    private MultilingualString description;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_title)
    private MultilingualString title;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_issued)
    private Instant releaseDate;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_modified)
    private Instant modifiedDate;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_language)
    private Set<String> languages;
    /*
     * Skipping publisher
     */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier)
    private URI identifier;
    /*
     * Skipping theme/category, type/genre, relation, qualified attribution,
     * license, rights
     */
    @OWLDataProperty(iri = Vocabulary.s_p_org_hasPart)
    private Set<URI> hasParts;
    /*
     * Skipping has policy, is referenced by
     */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_previousVersion)
    private URI previousVersion;
    /*
     * Skipping has version
     */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_hasCurrentVersion)
    private URI currentVersion;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_replaces)
    private URI replaces;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_version)
    private String version;
    /*
     * Skipping status, first, last, previous
     */
}
