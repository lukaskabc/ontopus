package cz.lukaskabc.ontology.ontopus.core_model.model.dcat;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntityWithDcatIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.DocumentedOWLClass;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/** Resource published or curated by a single agent. */
@MappedSuperclass
@DocumentedOWLClass(iri = Vocabulary.s_c_dcat_Resource_A)
public abstract class Resource<ID extends TypedIdentifier> extends PersistenceEntityWithDcatIdentifier<ID> {
    /*
     * skipping access rights, conforms to, contact point, creator
     */
    @OWLDataProperty(iri = Vocabulary.s_p_sioc_description)
    private MultilingualString description = new MultilingualString();

    @NotEmpty @OWLDataProperty(iri = Vocabulary.s_p_sioc_title)
    private MultilingualString title = new MultilingualString();

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_issued)
    private Instant releaseDate;

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_modified)
    private Instant modifiedDate;

    /*
     * Skipping publisher
     *
     * identifier implemented in persistence entity
     */
    /*
     * Skipping theme/category, type/genre, relation, qualified attribution,
     * license, rights, hasParts
     */
    /*
     * Skipping has policy, is referenced by
     */

    /*
     * Skipping has version, current version and replaces
     */

    /*
     * Skipping status, first, last, previous
     */

    public MultilingualString getDescription() {
        return description;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public Instant getReleaseDate() {
        return releaseDate;
    }

    public MultilingualString getTitle() {
        return title;
    }

    public void setDescription(MultilingualString description) {
        this.description = description;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setReleaseDate(Instant releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setTitle(MultilingualString title) {
        this.title = title;
    }
}
