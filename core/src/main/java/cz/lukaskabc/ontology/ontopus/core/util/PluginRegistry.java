package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.api.service.OntologyImporter;
import java.util.Collections;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PluginRegistry {
    private final Set<OntologyImporter> ontologyImporters;

    @Autowired
    public PluginRegistry(Set<OntologyImporter> ontologyImporters) {
        this.ontologyImporters = Collections.unmodifiableSet(ontologyImporters);
    }

    public Set<OntologyImporter> getOntologyImporters() {
        return ontologyImporters;
    }
}
