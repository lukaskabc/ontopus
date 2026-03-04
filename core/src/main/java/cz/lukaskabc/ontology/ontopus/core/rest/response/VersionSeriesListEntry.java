package cz.lukaskabc.ontology.ontopus.core.rest.response;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;

import java.net.URI;
import java.time.Instant;

public class VersionSeriesListEntry extends ResourceListEntry<VersionSeriesURI> {
    public VersionSeriesListEntry(
            VersionSeriesURI identifier, MultilingualString title, String version, Instant modifiedDate) {
        super(identifier, title, version, modifiedDate);
    }

    @Override
    protected VersionSeriesURI wrapIdentifier(URI identifier) {
        return new VersionSeriesURI(identifier);
    }
}
