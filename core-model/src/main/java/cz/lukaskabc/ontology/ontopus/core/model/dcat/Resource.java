package cz.lukaskabc.ontology.ontopus.core.model.dcat;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core.model.util.DocumentedOWLClass;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
     */
    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private URI identifier;
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
     * Skipping has version
     */
    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_hasCurrentVersion)
    private URI currentVersion;

    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_replaces)
    private URI replaces;

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_version, simpleLiteral = true)
    private String version;
    /*
     * Skipping status, first, last, previous
     */

    public URI getCurrentVersion() {
        return currentVersion;
    }

    public MultilingualString getDescription() {
        return description;
    }

    public Set<URI> getHasParts() {
        return hasParts;
    }

    public URI getIdentifier() {
        return identifier;
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

    public URI getReplaces() {
        return replaces;
    }

    public MultilingualString getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public void setCurrentVersion(URI currentVersion) {
        this.currentVersion = currentVersion;
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

    public void setReplaces(URI replaces) {
        this.replaces = replaces;
    }

    public void setTitle(MultilingualString title) {
        this.title = title;
    }

    @Override
    public void setUri(URI uri) {
        super.setUri(uri);
        this.identifier = uri;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
