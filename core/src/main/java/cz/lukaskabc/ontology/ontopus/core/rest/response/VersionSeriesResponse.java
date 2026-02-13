package cz.lukaskabc.ontology.ontopus.core.rest.response;

import java.net.URI;
import java.util.Set;

public class VersionSeriesResponse extends DatasetResponse {
    private URI ontologyURI;
    private URI last;
    private URI first;
    private Set<VersionArtifactListEntry> members;

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

    public void setFirst(URI first) {
        this.first = first;
    }

    public void setLast(URI last) {
        this.last = last;
    }

    public void setMembers(Set<VersionArtifactListEntry> members) {
        this.members = members;
    }

    public void setOntologyURI(URI ontologyURI) {
        this.ontologyURI = ontologyURI;
    }
}
