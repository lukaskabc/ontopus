package cz.lukaskabc.ontology.ontopus.core.rest.response;

import cz.cvut.kbss.jopa.model.MultilingualString;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

public class ResourceResponse extends EntityResponse {
    private final MultilingualString description;
    private final MultilingualString title;
    private final Instant releaseDate;
    private final Instant modifiedDate;
    private final Set<String> languages;
    private final URI previousVersion;
    private final String version;

    public ResourceResponse(
            URI uri,
            URI identifier,
            MultilingualString description,
            MultilingualString title,
            Instant releaseDate,
            Instant modifiedDate,
            Set<String> languages,
            URI previousVersion,
            String version) {
        super(uri, identifier);
        this.description = description;
        this.title = title;
        this.releaseDate = releaseDate;
        this.modifiedDate = modifiedDate;
        this.languages = languages;
        this.previousVersion = previousVersion;
        this.version = version;
    }

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
}
