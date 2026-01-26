package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionSeriesRepository;
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
