package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.api.util.VersionArtifactOptionsEntry;
import cz.lukaskabc.ontology.ontopus.api.util.VersionSeriesOptionsEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.mapper.DtoMapper;
import cz.lukaskabc.ontology.ontopus.core.rest.response.MenuOptionResponse;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EntityOptionsController {
    private final List<VersionSeriesOptionsEntry> seriesOptions;
    private final List<VersionArtifactOptionsEntry> artifactOptions;
    private final DtoMapper dtoMapper;

    public EntityOptionsController(
            List<VersionSeriesOptionsEntry> seriesOptions,
            List<VersionArtifactOptionsEntry> artifactOptions,
            DtoMapper dtoMapper) {
        this.seriesOptions = seriesOptions;
        this.artifactOptions = artifactOptions;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/series/artifacts/options")
    public Map<VersionArtifactURI, List<MenuOptionResponse>> findArtifactOptions(
            List<VersionArtifactURI> artifactIdentifiers) {
        final Map<VersionArtifactURI, List<MenuOptionResponse>> result = new HashMap<>(artifactOptions.size());
        for (VersionArtifactURI uri : artifactIdentifiers) {
            final List<MenuOptionResponse> options = artifactOptions.stream()
                    .filter(entry -> entry.showMenuEntry(uri))
                    .map(dtoMapper::toMenuOption)
                    .toList();
            result.put(uri, options);
        }
        return result;
    }

    @GetMapping("/series/options")
    public Map<VersionSeriesURI, List<MenuOptionResponse>> findSeriesOptions(List<VersionSeriesURI> seriesIdentifiers) {
        final Map<VersionSeriesURI, List<MenuOptionResponse>> result = new HashMap<>(artifactOptions.size());
        for (VersionSeriesURI uri : seriesIdentifiers) {
            final List<MenuOptionResponse> options = seriesOptions.stream()
                    .filter(entry -> entry.showMenuEntry(uri))
                    .map(dtoMapper::toMenuOption)
                    .toList();
            result.put(uri, options);
        }
        return result;
    }
}
