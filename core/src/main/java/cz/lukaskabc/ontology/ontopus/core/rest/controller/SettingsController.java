package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.util.SettingsEntry;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/settings")
@RestController
public class SettingsController extends AbstractJsonController {
    private final HashMap<String, SettingsEntry> entries;

    public SettingsController(List<SettingsEntry> beanEntries, ObjectMapper objectMapper) {
        super(objectMapper);
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
            entries.get(identifier).handleSubmit(parseData(request), request.getMultiFileMap());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
