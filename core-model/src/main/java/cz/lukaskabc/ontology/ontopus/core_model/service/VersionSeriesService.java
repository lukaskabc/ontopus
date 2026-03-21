package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionSeriesRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class VersionSeriesService extends BaseService<VersionSeriesURI, VersionSeries, VersionSeriesRepository> {
    public VersionSeriesService(VersionSeriesRepository repository) {
        super(repository);
    }

    /** Checks whether the given ontology identifier exists */
    public boolean isOntologyURI(ResourceURI resourceURI) {
        return repository.isOntologyURI(resourceURI);
    }
}
