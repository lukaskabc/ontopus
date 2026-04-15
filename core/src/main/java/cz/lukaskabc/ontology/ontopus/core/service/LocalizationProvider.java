package cz.lukaskabc.ontology.ontopus.core.service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class LocalizationProvider {
    private final Map<String, Map<String, String>> localization;

    public LocalizationProvider(Map<String, Map<String, String>> localization) {
        this.localization = localization;
    }

    public Set<String> getLanguages() {
        return localization.keySet();
    }

    /**
     * @param locale the requested language tag
     * @return all translations for the given language
     */
    public Optional<Map<String, String>> getLocale(String locale) {
        return Optional.ofNullable(localization.get(locale));
    }

    /**
     * Returns the underlying localization map
     *
     * @return {@code Map<LanguageTag, Map<TranslationKey, Message>>}
     */
    public Map<String, Map<String, String>> getLocalization() {
        return localization;
    }
}
