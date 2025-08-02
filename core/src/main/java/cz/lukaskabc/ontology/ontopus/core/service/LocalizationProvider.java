package cz.lukaskabc.ontology.ontopus.core.service;

import java.util.Map;

public class LocalizationProvider {
    private final Map<String, Map<String, String>> localization;

    public LocalizationProvider(Map<String, Map<String, String>> localization) {
        this.localization = localization;
    }

    public Map<String, String> getLocale(String locale) {
        return localization.get(locale);
    }
}
