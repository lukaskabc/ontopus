package cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping;

import cz.cvut.kbss.jopa.model.annotations.*;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.AbstractPersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ContextToControllerMappingURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURIImpl;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * Mapping of an ontology document, or its entities (defined by {@link #mappingType}, to controllers capable of handling
 * requests for the resource.
 */
@Context(Vocabulary.s_c_ContextToControllerMapping)
@OWLClass(iri = Vocabulary.s_c_ContextToControllerMapping)
public class ContextToControllerMapping extends AbstractPersistenceEntity<ContextToControllerMappingURI> {
    /** The ontology graph */
    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_subject)
    private URI subject;

    /** The controller capable of handling the resource */
    @NotEmpty @OWLObjectProperty(iri = Vocabulary.s_p_mappedBy, fetch = FetchType.EAGER)
    private Set<ControllerDescription> controllers = new HashSet<>();

    /** The type of the mapping */
    @NotNull @Enumerated(EnumType.OBJECT_ONE_OF)
    @OWLObjectProperty(iri = Vocabulary.s_p_uriMappingType)
    private MappingType mappingType;

    public void addController(ControllerDescription controller) {
        this.controllers.add(controller);
    }

    public Set<ControllerDescription> getControllers() {
        return controllers;
    }

    public MappingType getMappingType() {
        return mappingType;
    }

    public GraphURI getSubject() {
        return new GraphURIImpl(subject);
    }

    public void removeController(ControllerDescription controller) {
        this.controllers.remove(controller);
    }

    public void setControllers(Set<ControllerDescription> controllers) {
        this.controllers = controllers;
    }

    public void setMappingType(MappingType mappingType) {
        this.mappingType = mappingType;
    }

    public void setSubject(GraphURI graphURI) {
        this.subject = graphURI.toURI();
    }

    @Override
    protected ContextToControllerMappingURI wrapUri(URI uri) {
        return new ContextToControllerMappingURI(uri);
    }
}
