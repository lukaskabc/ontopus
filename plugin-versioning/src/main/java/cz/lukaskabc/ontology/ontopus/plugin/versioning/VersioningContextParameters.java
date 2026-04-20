package cz.lukaskabc.ontology.ontopus.plugin.versioning;

public class VersioningContextParameters {
    public static final String VERSION_PREDICATE = "VersionPredicate";

    public static final String VERSION_IRI_PREDICATE = "VersionIRIPredicate";
    public static final String PREVIOUS_VERSION_IRI_PREDICATE = "PreviousVersionIRIPredicate";

    private VersioningContextParameters() {
        throw new AssertionError();
    }
}
