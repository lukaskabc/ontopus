package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.core.service.LocalizationProvider;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/locale")
public class LocaleController {
    private static final String SERVER_NAMESPACE = "server";
    private final LocalizationProvider localizationProvider;

    @Autowired
    public LocaleController(LocalizationProvider localizationProvider) {
        this.localizationProvider = localizationProvider;
    }

    @GetMapping("/{namespace}/{locale}.json")
    public ResponseEntity<@NonNull Map<String, String>> getLocale(
            @PathVariable(name = "namespace") String namespace, @PathVariable(name = "locale") String locale) {
        if (SERVER_NAMESPACE.equals(namespace)) {
            return localizationProvider
                    .getLocale(locale)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.notFound().build();
    }
}
