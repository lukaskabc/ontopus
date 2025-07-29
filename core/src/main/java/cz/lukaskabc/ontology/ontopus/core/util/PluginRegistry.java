package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.api.service.OntologyImporter;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class PluginRegistry {
    private final Set<OntologyImporter<?>> ontologyImporters;

    @Autowired
    public PluginRegistry(Set<OntologyImporter<?>> ontologyImporters) {
        this.ontologyImporters = Collections.unmodifiableSet(ontologyImporters);
    }
}
