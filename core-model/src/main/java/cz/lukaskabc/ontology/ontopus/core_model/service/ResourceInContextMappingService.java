package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.ResourceInContextMappingRepository;
import org.springframework.stereotype.Service;

@Service
public class ResourceInContextMappingService {
    private final ResourceInContextMappingRepository repository;

    public ResourceInContextMappingService(ResourceInContextMappingRepository repository) {
        this.repository = repository;
    }
}
