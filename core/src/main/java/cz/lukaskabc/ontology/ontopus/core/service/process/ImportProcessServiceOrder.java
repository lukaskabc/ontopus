package cz.lukaskabc.ontology.ontopus.core.service.process;

public class ImportProcessServiceOrder {
    private static final int SINGLE_STAGE = 10000000;
    public static final int DATA_LOADING_SELECTION_SERVICE = SINGLE_STAGE;
    public static final int ONTOLOGY_IDENTIFIER_RESOLVING_SERVICE = DATA_LOADING_SELECTION_SERVICE + SINGLE_STAGE;
    public static final int ARTIFACT_BUILDING_SELECTION_SERVICE = ONTOLOGY_IDENTIFIER_RESOLVING_SERVICE + SINGLE_STAGE;

    private ImportProcessServiceOrder() {
        throw new AssertionError();
    }
}
