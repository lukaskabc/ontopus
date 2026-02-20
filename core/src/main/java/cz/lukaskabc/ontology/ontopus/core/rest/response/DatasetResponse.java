package cz.lukaskabc.ontology.ontopus.core.rest.response;

import cz.cvut.kbss.jopa.model.MultilingualString;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

public class DatasetResponse extends ResourceResponse {
    private final URI series;

    public DatasetResponse(
            URI uri,
            URI identifier,
            MultilingualString description,
            MultilingualString title,
            Instant releaseDate,
            Instant modifiedDate,
            Set<String> languages,
            URI previousVersion,
            String version,
            URI series) {
        super(uri, identifier, description, title, releaseDate, modifiedDate, languages, previousVersion, version);
        this.series = series;
    }

    public URI getSeries() {
        return series;
    }
}
