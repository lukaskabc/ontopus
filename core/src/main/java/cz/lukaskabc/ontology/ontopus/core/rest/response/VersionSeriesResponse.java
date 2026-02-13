package cz.lukaskabc.ontology.ontopus.core.rest.response;

import java.net.URI;
import java.util.Set;

public class VersionSeriesResponse extends DatasetResponse {
    private URI last;
    private URI first;
    private Set<URI> members;

    public URI getFirst() {
        return first;
    }

    public URI getLast() {
        return last;
    }

    public Set<URI> getMembers() {
        return members;
    }

    public void setFirst(URI first) {
        this.first = first;
    }

    public void setLast(URI last) {
        this.last = last;
    }

    public void setMembers(Set<URI> members) {
        this.members = members;
    }
}
