package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.core.model.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.repository.VersionSeriesRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class VersionSeriesService {
    private final VersionSeriesRepository repository;

    public VersionSeriesService(VersionSeriesRepository repository) {
        this.repository = repository;
    }

    @Nullable public VersionSeries find(@Nullable VersionSeriesURI uri) {
        return repository.find(uri);
    }
}
