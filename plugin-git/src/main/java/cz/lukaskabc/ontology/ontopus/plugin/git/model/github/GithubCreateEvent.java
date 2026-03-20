package cz.lukaskabc.ontology.ontopus.plugin.git.model.github;

public class GithubCreateEvent extends GithubRefEventBase {
    private String refType;

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }
}
