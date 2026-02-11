package cz.lukaskabc.ontology.ontopus.core.rest.response;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;

import java.io.Serializable;
import java.net.URI;
import java.time.Instant;

public class VersionSeriesListEntry implements Serializable {
    private final URI identifier;
    private final MultilingualString title;
    private final MultilingualString description;
    private final String version;
    private final Instant modifiedDate;

    public VersionSeriesListEntry(
            VersionSeriesURI identifier,
            MultilingualString title,
            MultilingualString description,
            String version,
            Instant modifiedDate) {
        this.identifier = identifier.toURI();
        this.title = title;
        this.description = description;
        this.version = version;
        this.modifiedDate = modifiedDate;
    }

    public MultilingualString getDescription() {
        return description;
    }

    public URI getIdentifier() {
        return identifier;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public MultilingualString getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }
}
