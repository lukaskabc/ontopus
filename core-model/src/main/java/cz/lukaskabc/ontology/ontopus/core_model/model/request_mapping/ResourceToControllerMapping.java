package cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping;

import cz.cvut.kbss.jopa.model.annotations.*;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

/**
 * Mapping of an ontology document, or its entities (defined by {@link #mappingType}, to controllers capable of handling
 * requests for the resource.
 */
@OWLClass(iri = Vocabulary.s_c_UriMapping)
public class ResourceToControllerMapping {
    /** The resource being mapped */
    @Id
    @NotNull private URI subject;

    /** The controller capable of handling the resource */
    @NotEmpty @OWLObjectProperty(iri = Vocabulary.s_p_mappedBy)
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

    public URI getSubject() {
        return subject;
    }

    public void setControllers(Set<Controller> controllers) {
        this.controllers = controllers;
    }

    public void setMappingType(MappingType mappingType) {
        this.mappingType = mappingType;
    }

    public void setSubject(URI subject) {
        this.subject = subject;
    }
}
