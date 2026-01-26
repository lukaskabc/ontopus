package cz.lukaskabc.ontology.ontopus.core_model.model.dcat;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.DocumentedOWLClass;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.Instant;
import java.util.Set;

/** Resource published or curated by a single agent. */
@MappedSuperclass
@DocumentedOWLClass(iri = Vocabulary.s_c_dcat_Resource_A)
public abstract class Resource<ID extends EntityIdentifier> extends PersistenceEntity<ID> {
    /*
     * skipping access rights, conforms to, contact point, creator
     */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_description)
    private MultilingualString description;

    @NotEmpty @OWLDataProperty(iri = Vocabulary.s_p_dcat_title)
    private MultilingualString title;

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_issued)
    private Instant releaseDate;

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_modified)
    private Instant modifiedDate;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_language)
    private Set<String> languages;
    /*
     * Skipping publisher
     *
     * identifier implemented in persistence entity
     */
    /*
     * Skipping theme/category, type/genre, relation, qualified attribution,
     * license, rights
     */
    @OWLObjectProperty(iri = Vocabulary.s_p_org_hasPart)
    private Set<URI> hasParts;
    /*
     * Skipping has policy, is referenced by
     */

    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_previousVersion)
    private URI previousVersion;
    /*
     * Skipping has version, current version and replaces
     */

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_version, simpleLiteral = true)
    private String version;
    /*
     * Skipping status, first, last, previous
     */

    public MultilingualString getDescription() {
        return description;
    }

    public Set<URI> getHasParts() {
        return hasParts;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public URI getPreviousVersion() {
        return previousVersion;
    }

    public Instant getReleaseDate() {
        return releaseDate;
    }

    public MultilingualString getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public void setDescription(MultilingualString description) {
        this.description = description;
    }

    public void setHasParts(Set<URI> hasParts) {
        this.hasParts = hasParts;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setPreviousVersion(URI previousVersion) {
        this.previousVersion = previousVersion;
    }

    public void setReleaseDate(Instant releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setTitle(MultilingualString title) {
        this.title = title;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
