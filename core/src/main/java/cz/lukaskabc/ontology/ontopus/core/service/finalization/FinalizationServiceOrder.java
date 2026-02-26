package cz.lukaskabc.ontology.ontopus.core.service.finalization;

import org.springframework.core.Ordered;

public class FinalizationServiceOrder {
    private static final int SINGLE_STAGE = 10000000;
    public static final int DATABASE_CONTEXT_PERSIST = Ordered.LOWEST_PRECEDENCE - SINGLE_STAGE;
    private FinalizationServiceOrder() {
        throw new AssertionError();
    }
}
