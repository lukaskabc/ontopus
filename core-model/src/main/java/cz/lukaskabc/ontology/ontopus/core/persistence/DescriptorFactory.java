package cz.lukaskabc.ontology.ontopus.core.persistence;

import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.lukaskabc.ontology.ontopus.core.model.*;
import org.springframework.stereotype.Component;

@Component
public class DescriptorFactory {
    private final EntityDescriptor ontologyArtifact;
    private final EntityDescriptor ontologyArtifactCatalog;
    private final EntityDescriptor ontologyVersionSeries;
    private final EntityDescriptor ontologyDistribution;
    private final EntityDescriptor temporaryContext;
    private final EntityDescriptor user;

    public DescriptorFactory() {
        ontologyArtifact = new EntityDescriptor(OntologyArtifact_.entityClassIRI.toURI());
        ontologyArtifactCatalog = new EntityDescriptor(OntologyArtifactCatalog_.entityClassIRI.toURI());
        ontologyVersionSeries = new EntityDescriptor(OntologyVersionSeries_.entityClassIRI.toURI());
        ontologyDistribution = new EntityDescriptor(OntologyDistribution_.entityClassIRI.toURI());
        temporaryContext = new EntityDescriptor(TemporaryContext_.entityClassIRI.toURI());
        user = new EntityDescriptor(User_.entityClassIRI.toURI());

        ontologyArtifact.addAttributeDescriptor(OntologyArtifact_.distributions, ontologyDistribution);
    }

    public EntityDescriptor ontologyArtifact() {
        return ontologyArtifact;
    }

    public EntityDescriptor ontologyArtifactCatalog() {
        return ontologyArtifactCatalog;
    }

    public EntityDescriptor ontologyDistribution() {
        return ontologyDistribution;
    }

    public EntityDescriptor ontologyVersionSeries() {
        return ontologyVersionSeries;
    }

    public EntityDescriptor temporaryContext() {
        return temporaryContext;
    }

    public EntityDescriptor user() {
        return user;
    }
}
