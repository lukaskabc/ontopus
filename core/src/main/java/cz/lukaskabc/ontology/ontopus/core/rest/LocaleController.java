package cz.lukaskabc.ontology.ontopus.core.rest;

import cz.lukaskabc.ontology.ontopus.core.model.LocalizationProvider;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/locale")
public class LocaleController {
    private final LocalizationProvider localizationProvider;

    @Autowired
    public LocaleController(LocalizationProvider localizationProvider) {
        this.localizationProvider = localizationProvider;
    }

    @GetMapping("/{locale}.json")
    public Map<String, String> getLocale(@PathVariable(name = "locale") String locale) {
        return localizationProvider.getLocale(locale);
    }
}
