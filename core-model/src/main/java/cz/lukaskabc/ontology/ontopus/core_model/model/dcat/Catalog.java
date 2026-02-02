package cz.lukaskabc.ontology.ontopus.core_model.model.dcat;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.DocumentedOWLClass;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

@MappedSuperclass
@DocumentedOWLClass(iri = Vocabulary.s_c_dcat_Catalog)
public abstract class Catalog<CatalogDistributionIdentifier extends EntityIdentifier, ID extends EntityIdentifier>
        extends Dataset<CatalogDistributionIdentifier, ID> {
    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_homepage)
    private URI homepage;

    public URI getHomepage() {
        return homepage;
    }

    public void setHomepage(URI homepage) {
        this.homepage = homepage;
    }
}
