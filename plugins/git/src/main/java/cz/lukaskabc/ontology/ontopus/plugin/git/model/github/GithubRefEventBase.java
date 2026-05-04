package cz.lukaskabc.ontology.ontopus.plugin.git.model.github;

public abstract class GithubRefEventBase {
    private String ref;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
