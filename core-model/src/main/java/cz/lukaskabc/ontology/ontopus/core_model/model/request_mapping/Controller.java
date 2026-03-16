package cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.AbstractGeneratedPersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ControllerURI;
import org.springframework.http.MediaType;

import jakarta.validation.constraints.NotEmpty;
import java.net.URI;
import java.util.Set;

/** Controller description capable of retrieving a resource in a supported media type */
@OWLClass(iri = Vocabulary.s_c_Controller)
public class Controller extends AbstractGeneratedPersistenceEntity<ControllerURI> {
    /** The fully qualified name of the Java class */
    @NotEmpty @OWLDataProperty(iri = Vocabulary.s_p_javaClassName, simpleLiteral = true)
    private String className;

    /** Supported media types for retrieving the resource */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_format, simpleLiteral = true)
    private Set<MediaType> supportedMediaTypes;

    public String getClassName() {
        return className;
    }

    public Set<MediaType> getSupportedMediaTypes() {
        return supportedMediaTypes;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSupportedMediaTypes(Set<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }

    @Override
    protected ControllerURI wrapUri(URI uri) {
        return new ControllerURI(uri);
    }
}
