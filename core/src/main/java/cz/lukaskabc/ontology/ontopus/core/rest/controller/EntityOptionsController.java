package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.util.VersionSeriesOptionsEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.mapper.DtoMapper;
import cz.lukaskabc.ontology.ontopus.core.rest.response.MenuOptionResponse;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EntityOptionsController extends AbstractJsonController {
    private final Map<String, VersionSeriesOptionsEntry> seriesOptions = new HashMap<>();
    private final DtoMapper dtoMapper;

    public EntityOptionsController(
            List<VersionSeriesOptionsEntry> seriesOptions, DtoMapper dtoMapper, ObjectMapper objectMapper) {
        super(objectMapper);
        seriesOptions.forEach(series -> this.seriesOptions.put(series.getIdentifier(), series));
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/series/options")
    public Map<VersionSeriesURI, List<MenuOptionResponse>> findSeriesOptions(
            @RequestParam("series") List<VersionSeriesURI> seriesIdentifiers) {
        final Map<VersionSeriesURI, List<MenuOptionResponse>> result = new HashMap<>(seriesOptions.size());
        for (VersionSeriesURI uri : seriesIdentifiers) {
            final List<MenuOptionResponse> options = seriesOptions.values().stream()
                    .filter(entry -> entry.showMenuEntry(uri))
                    .map(dtoMapper::toMenuOption)
                    .toList();
            result.put(uri, options);
        }
        return result;
    }

    @GetMapping("/series/options/{formIdentifier}")
    public ResponseEntity<JsonForm> getSeriesOptionsForm(
            @PathVariable("formIdentifier") String formIdentifier,
            @RequestParam("series") VersionSeriesURI seriesIdentifier) {
        final VersionSeriesOptionsEntry entry = seriesOptions.get(formIdentifier);
        if (entry != null) {
            return ResponseEntity.ok(entry.getForm(seriesIdentifier));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/series/options/{formIdentifier}")
    public ResponseEntity<?> handleSeriesOptionsFormSubmit(
            @PathVariable("formIdentifier") String formIdentifier,
            @RequestParam("series") VersionSeriesURI seriesIdentifier,
            MultipartHttpServletRequest request) {
        final VersionSeriesOptionsEntry entry = seriesOptions.get(formIdentifier);
        if (entry != null) {
            entry.handleSubmit(seriesIdentifier, parseData(request), request.getMultiFileMap());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
