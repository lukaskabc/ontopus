package cz.lukaskabc.ontology.ontopus.core.rest.response;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;

import java.io.Serializable;
import java.net.URI;
import java.time.Instant;

public abstract class ResourceListEntry<I extends EntityIdentifier> implements Serializable {
    private final URI identifier;
    private final MultilingualString title;
    private final String version;
    private final Instant modifiedDate;

    public ResourceListEntry(I identifier, MultilingualString title, String version, Instant modifiedDate) {
        this.identifier = identifier.toURI();
        this.title = title;
        this.version = version;
        this.modifiedDate = modifiedDate;
    }

    public I getIdentifier() {
        return wrapIdentifier(identifier);
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

    protected abstract I wrapIdentifier(URI identifier);
}
