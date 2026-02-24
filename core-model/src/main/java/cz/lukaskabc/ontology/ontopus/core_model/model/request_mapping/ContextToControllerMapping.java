package cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping;

import cz.cvut.kbss.jopa.model.annotations.*;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ContextToControllerMappingURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

/**
 * Mapping of an ontology document, or its entities (defined by {@link #mappingType}, to controllers capable of handling
 * requests for the resource.
 */
@OWLClass(iri = Vocabulary.s_c_ContextToControllerMapping)
public class ContextToControllerMapping extends PersistenceEntity<ContextToControllerMappingURI> {
    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_subject)
    private URI subject;

    /** The controller capable of handling the resource */
    @NotEmpty @OWLObjectProperty(iri = Vocabulary.s_p_mappedBy, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Controller> controllers;

    /** The type of the mapping */
    @NotNull @Enumerated(EnumType.OBJECT_ONE_OF)
    @OWLObjectProperty(iri = Vocabulary.s_p_uriMappingType)
    private MappingType mappingType;

    public Set<Controller> getControllers() {
        return controllers;
    }

    public MappingType getMappingType() {
        return mappingType;
    }

    public GraphURI getSubject() {
        return new GraphURI(subject);
    }

    public void setControllers(Set<Controller> controllers) {
        this.controllers = controllers;
    }

    public void setMappingType(MappingType mappingType) {
        this.mappingType = mappingType;
    }

    public void setSubject(GraphURI subject) {
        this.subject = subject.toURI();
    }

    @Override
    protected ContextToControllerMappingURI wrapUri(URI uri) {
        return new ContextToControllerMappingURI(uri);
    }
}
