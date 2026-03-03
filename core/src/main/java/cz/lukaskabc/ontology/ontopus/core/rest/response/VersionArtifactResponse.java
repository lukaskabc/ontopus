package cz.lukaskabc.ontology.ontopus.core.rest.response;

import cz.cvut.kbss.jopa.model.MultilingualString;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

public class VersionArtifactResponse extends DatasetResponse {
    // TODO add distributions
    /*
     * private final Set<DistributionResponse> distributions;
     *
     * public VersionArtifactResponse(URI uri, URI identifier, MultilingualString
     * description, MultilingualString title, Instant releaseDate, Instant
     * modifiedDate, Set<String> languages, URI previousVersion, String version, URI
     * series, Set<DistributionResponse> distributions) { super(uri, identifier,
     * description, title, releaseDate, modifiedDate, languages, previousVersion,
     * version, series); this.distributions = distributions; }
     *
     * public Set<DistributionResponse> getDistributions() { return distributions; }
     */

    public VersionArtifactResponse(
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
    }
}
