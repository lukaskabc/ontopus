package cz.lukaskabc.ontology.ontopus.core.service.finalization;

public class FinalizationServiceOrder {
    private static final int SINGLE_STAGE = 10000000;

    public static final int CONTEXT_SERIALIZATION = 0;
    public static final int VERSION_SERIES_UPDATE = CONTEXT_SERIALIZATION + SINGLE_STAGE;
    public static final int DATABASE_CONTEXT_PERSIST = VERSION_SERIES_UPDATE + SINGLE_STAGE;
    public static final int CONTEXT_RESOURCES_MAPPING = DATABASE_CONTEXT_PERSIST + SINGLE_STAGE;

    private FinalizationServiceOrder() {
        throw new AssertionError();
    }
}
