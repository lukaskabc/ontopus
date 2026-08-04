package cz.lukaskabc.ontology.ontopus.core.import_process;

public class ImportProcessServiceOrder {
    private static final int SINGLE_STAGE = 1000;
    public static final int DATA_LOADING_SELECTION_SERVICE = SINGLE_STAGE;
    public static final int ONTOLOGY_IDENTIFIER_RESOLVING_SERVICE = DATA_LOADING_SELECTION_SERVICE + SINGLE_STAGE;
    public static final int ARTIFACT_BUILDING_SERVICE = ONTOLOGY_IDENTIFIER_RESOLVING_SERVICE + SINGLE_STAGE;
    public static final int SERIES_BUILDING_SERVICE = ARTIFACT_BUILDING_SERVICE + SINGLE_STAGE;
    public static final int ARTIFACT_VERSIONING_SELECTION_SERVICE = SERIES_BUILDING_SERVICE + SINGLE_STAGE;
    public static final int VERSION_URI_STRATEGY_SELECTION_SERVICE =
            ARTIFACT_VERSIONING_SELECTION_SERVICE + SINGLE_STAGE;
    public static final int EXISTING_ONTOLOGY_RESOLVING_SERVICE = VERSION_URI_STRATEGY_SELECTION_SERVICE + SINGLE_STAGE;

    // the last step
    public static final int ANNOTATION_INJECTION_SERVICE = Integer.MAX_VALUE - SINGLE_STAGE;
    public static final int ARTIFACT_REVIEW_SERVICE = ANNOTATION_INJECTION_SERVICE - SINGLE_STAGE;
    public static final int VERSION_SERIES_UPDATE = ARTIFACT_REVIEW_SERVICE - SINGLE_STAGE;

    private ImportProcessServiceOrder() {
        throw new AssertionError();
    }
}
