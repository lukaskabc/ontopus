package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.util.SettingsEntry;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/settings")
@RestController
public class SettingsController {
    private final HashMap<String, SettingsEntry> entries;

    public SettingsController(List<SettingsEntry> beanEntries) {
        this.entries = new HashMap<>();
        for (SettingsEntry entry : beanEntries) {
            this.entries.put(entry.getIdentifier(), entry);
        }
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<JsonForm> getSettingsForm(@PathVariable("identifier") String identifier) {
        if (entries.containsKey(identifier)) {
            return ResponseEntity.ok(entries.get(identifier).getForm());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public Map<String, String> list() {
        final Map<String, String> result = new HashMap<>(entries.size());
        entries.forEach((identifier, settingsEntry) -> {
            result.put(identifier, settingsEntry.getLabel());
        });
        return result;
    }

    @PostMapping(
            path = "/{identifier}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> submitSettingsForm(
            @PathVariable("identifier") String identifier, MultipartHttpServletRequest request) {
        if (entries.containsKey(identifier)) {
            // TODO delegate to settings service, parse data and construct form result
            // entries.get(uuid).handleSubmit(new FormResult(request.getParameterMap(),
            // request.getFileMap()));
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
