package cz.lukaskabc.ontology.ontopus.core.rest;

import cz.lukaskabc.ontology.ontopus.core.rest.response.VersionSeriesListEntry;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionSeriesRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EntityController {
    private final VersionSeriesRepository versionSeriesRepository;
    private final DtoMapper dtoMapper;

    public EntityController(VersionSeriesRepository versionSeriesRepository, DtoMapper dtoMapper) {
        this.versionSeriesRepository = versionSeriesRepository;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/ontologies")
    public Page<@NonNull VersionSeriesListEntry> versionSeriesList(Pageable pageable) {
        return versionSeriesRepository.find(pageable).map(dtoMapper::versionSeriesToListEntry);
    }
}
