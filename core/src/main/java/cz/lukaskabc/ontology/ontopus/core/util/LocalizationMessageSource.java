package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.core.service.LocalizationProvider;
import org.jspecify.annotations.Nullable;
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

    /**
     * Try to retrieve the given message using basic language Locale tag. Otherwise, try to retrieve the given message
     * from the parent {@code MessageSource}, if any.
     *
     * @param code the code to lookup up, such as 'calculator.noRateSet'
     * @param args array of arguments that will be filled in for params within the message
     * @param locale the locale in which to do the lookup
     * @return the resolved message, or {@code null} if not found
     * @see #getParentMessageSource()
     */
    @Override
    protected @Nullable String getMessageFromParent(String code, Object @Nullable [] args, Locale locale) {
        final Locale baseLocale = Locale.of(locale.getLanguage());
        if (!baseLocale.equals(locale)) {
            return getMessageInternal(code, args, baseLocale);
        }
        return super.getMessageFromParent(code, args, locale);
    }
}
