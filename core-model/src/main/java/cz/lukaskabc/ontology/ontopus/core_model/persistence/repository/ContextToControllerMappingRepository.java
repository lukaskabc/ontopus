package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ContextToControllerMappingURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.MappingType;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.ContextToControllerMappingDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.ContextToControllerMappingUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.MappingControllerUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Optional;

@Repository
public class ContextToControllerMappingRepository
        extends AbstractRepository<
                ContextToControllerMappingURI, ContextToControllerMapping, ContextToControllerMappingDao> {
    private final MappingControllerUriGenerator controllerUriGenerator;

    public ContextToControllerMappingRepository(
            ContextToControllerMappingDao dao,
            Validator validator,
            ContextToControllerMappingUriGenerator identifierGenerator,
            MappingControllerUriGenerator controllerUriGenerator) {
        super(dao, validator, identifierGenerator);
        this.controllerUriGenerator = controllerUriGenerator;
    }

    @Transactional(readOnly = true)
    public ContextToControllerMapping findByTypeAndContext(MappingType mappingType, GraphURI graphURI) {
        return Optional.ofNullable(dao.findByTypeAndContext(mappingType, graphURI))
                .orElseThrow(() -> new NotFoundException(
                        "ContextToControllerMapping not found for type " + mappingType + " and context " + graphURI));
    }

    @Override
    protected void setIdentifierIfMissing(ContextToControllerMapping entity) {
        super.setIdentifierIfMissing(entity);
        entity.getControllers().forEach(controllerUriGenerator::setIdentifierIfMissing);
    }

    @Override
    protected <T> T validated(T object) {
        final ContextToControllerMapping entity = (ContextToControllerMapping) object;
        if (MappingType.RESOURCE.equals(entity.getMappingType())
                && entity.getSubjects().size() != 1) {
            // aliases may be present only for ontology document mappings
            throw new ValidationException(
                    "Resource mapping must have exactly one subject, aliases are not allowed for resource mappings.");
        }
        return super.validated(object);
    }
}
