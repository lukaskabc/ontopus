package cz.lukaskabc.ontology.ontopus.core_model.persistence;

import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.lukaskabc.ontology.ontopus.core_model.model.*;
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
        ontologyArtifact = new EntityDescriptor(VersionArtifact_.entityClassIRI.toURI());
        ontologyArtifactCatalog = new EntityDescriptor(OntopusCatalog_.entityClassIRI.toURI());
        ontologyVersionSeries = new EntityDescriptor(VersionSeries_.entityClassIRI.toURI());
        ontologyDistribution = new EntityDescriptor(OntologyDistribution_.entityClassIRI.toURI());
        temporaryContext = new EntityDescriptor(TemporaryContext_.entityClassIRI.toURI());
        user = new EntityDescriptor(User_.entityClassIRI.toURI());
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
