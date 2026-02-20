package cz.lukaskabc.ontology.ontopus.core.rest.response;

import cz.cvut.kbss.jopa.model.MultilingualString;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

public class VersionSeriesResponse extends DatasetResponse {
    private final URI ontologyURI;
    private final URI last;
    private final URI first;
    private final Set<VersionArtifactListEntry> members;

    public VersionSeriesResponse(
            URI uri,
            URI identifier,
            MultilingualString description,
            MultilingualString title,
            Instant releaseDate,
            Instant modifiedDate,
            Set<String> languages,
            URI previousVersion,
            String version,
            URI series,
            URI ontologyURI,
            URI last,
            URI first,
            Set<VersionArtifactListEntry> members) {
        super(
                uri,
                identifier,
                description,
                title,
                releaseDate,
                modifiedDate,
                languages,
                previousVersion,
                version,
                series);
        this.ontologyURI = ontologyURI;
        this.last = last;
        this.first = first;
        this.members = members;
    }

    public URI getFirst() {
        return first;
    }

    public URI getLast() {
        return last;
    }

    public Set<VersionArtifactListEntry> getMembers() {
        return members;
    }

    public URI getOntologyURI() {
        return ontologyURI;
    }
}
