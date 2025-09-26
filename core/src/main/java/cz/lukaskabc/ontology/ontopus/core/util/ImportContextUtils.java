package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;

public class ImportContextUtils {
    public static String getIndexedServiceIdentifier(ImportProcessingService<?> service, int index) {
        return service.getUniqueIdentifier() + "#" + index;
    }

    private ImportContextUtils() {
        throw new AssertionError();
    }
}
