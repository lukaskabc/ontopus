package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.core.service.LocalizationProvider;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component("messageSource")
public class LocalizationMessageSource extends StaticMessageSource {
    public LocalizationMessageSource(LocalizationProvider localizationProvider) {
        setUseCodeAsDefaultMessage(false);
        localizationProvider.getLanguages().stream()
                .map(langTag -> Map.entry(Locale.forLanguageTag(langTag), localizationProvider.getLocale(langTag)))
                .filter(entry -> entry.getValue().isPresent())
                .forEach(entry -> addMessages(entry.getValue().get(), entry.getKey()));
    }
}
