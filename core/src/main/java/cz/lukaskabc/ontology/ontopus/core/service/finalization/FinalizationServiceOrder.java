package cz.lukaskabc.ontology.ontopus.core.service.finalization;

import org.springframework.core.Ordered;

public class FinalizationServiceOrder {
    private static final int SINGLE_STAGE = 10000000;

    public static final int CONTEXT_SERIALIZATION = 0;
    public static final int VERSION_SERIES_UPDATE = CONTEXT_SERIALIZATION + SINGLE_STAGE;

    public static final int CONTEXT_RESOURCES_MAPPING = Ordered.LOWEST_PRECEDENCE - SINGLE_STAGE;
    public static final int DATABASE_CONTEXT_PERSIST = CONTEXT_RESOURCES_MAPPING - SINGLE_STAGE;

    private FinalizationServiceOrder() {
        throw new AssertionError();
    }
}
