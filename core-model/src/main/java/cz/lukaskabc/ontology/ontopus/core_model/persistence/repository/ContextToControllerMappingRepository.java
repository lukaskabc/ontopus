package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ContextToControllerMappingURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.MappingType;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.ContextToControllerMappingDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.IdentifierGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Optional;

@Repository
public class ContextToControllerMappingRepository
        extends AbstractRepository<
                ContextToControllerMappingURI, ContextToControllerMapping, ContextToControllerMappingDao> {

    public ContextToControllerMappingRepository(
            ContextToControllerMappingDao dao,
            Validator validator,
            IdentifierGenerator<ContextToControllerMappingURI, ContextToControllerMapping> identifierGenerator) {
        super(dao, validator, identifierGenerator);
    }

    @Transactional(readOnly = true)
    public ContextToControllerMapping findByTypeAndContext(MappingType mappingType, GraphURI graphURI) {
        return Optional.ofNullable(dao.findByTypeAndContext(mappingType, graphURI))
                .orElseThrow(() -> new NotFoundException(
                        "ContextToControllerMapping not found for type " + mappingType + " and context " + graphURI));
    }
}
