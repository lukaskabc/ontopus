package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionSeriesRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseEntityService;
import org.springframework.stereotype.Service;

@Service
public class VersionSeriesService extends BaseEntityService<VersionSeriesURI, VersionSeries, VersionSeriesRepository> {
    public VersionSeriesService(VersionSeriesRepository repository) {
        super(repository);
    }
}
