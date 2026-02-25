package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.core.rest.mapper.DtoMapper;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionSeriesListEntry;
import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionSeriesResponse;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VersionSeriesController {
    private final VersionSeriesService versionSeriesService;
    private final DtoMapper dtoMapper;

    public VersionSeriesController(VersionSeriesService versionSeriesService, DtoMapper dtoMapper) {
        this.versionSeriesService = versionSeriesService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/ontology")
    public VersionSeriesResponse getVersionSeries(@RequestParam("identifier") String identifier) {
        // TODO: rework version series identifiers, they are different entities than the
        // ontology them selfs,
        // so they should have unique internal URIs
        //

        final VersionSeriesURI uri = new VersionSeriesURI(identifier);
        final VersionSeries series = versionSeriesService.findRequiredById(uri);
        return dtoMapper.versionSeriesToResponse(series);
    }

    @GetMapping("/ontologies")
    public Page<@NonNull VersionSeriesListEntry> versionSeriesList(
            Pageable pageable, @RequestParam(value = "filter", required = false) List<String> filter) {
        return versionSeriesService.find(pageable, filter).map(dtoMapper::versionSeriesToListEntry);
    }
}
