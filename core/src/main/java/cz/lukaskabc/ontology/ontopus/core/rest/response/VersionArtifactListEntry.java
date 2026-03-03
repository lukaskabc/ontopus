package cz.lukaskabc.ontology.ontopus.core.rest.response;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;

import java.net.URI;
import java.time.Instant;

public class VersionArtifactListEntry extends ResourceListEntry<VersionArtifactURI> {
    public VersionArtifactListEntry(
            VersionArtifactURI identifier,
            MultilingualString title,
            MultilingualString description,
            String version,
            Instant modifiedDate) {
        super(identifier, title, description, version, modifiedDate);
    }

    @Override
    protected VersionArtifactURI wrapIdentifier(URI identifier) {
        return new VersionArtifactURI(identifier);
    }
}
