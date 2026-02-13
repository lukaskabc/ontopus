package cz.lukaskabc.ontology.ontopus.core.rest.response;

import cz.cvut.kbss.jopa.model.MultilingualString;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

public class ResourceResponse extends EntityResponse {
    private MultilingualString description;
    private MultilingualString title;
    private Instant releaseDate;
    private Instant modifiedDate;
    private Set<String> languages;
    private URI previousVersion;
    private String version;

    public MultilingualString getDescription() {
        return description;
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
