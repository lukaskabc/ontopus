package cz.lukaskabc.ontology.ontopus.core.service;

import java.util.Map;
import java.util.Optional;

public class LocalizationProvider {
    private final Map<String, Map<String, String>> localization;

    public LocalizationProvider(Map<String, Map<String, String>> localization) {
        this.localization = localization;
    }

    public Optional<Map<String, String>> getLocale(String locale) {
        return Optional.ofNullable(localization.get(locale));
    }
}
