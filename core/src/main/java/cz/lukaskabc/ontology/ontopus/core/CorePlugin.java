package cz.lukaskabc.ontology.ontopus.core;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import java.util.List;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CorePlugin implements Plugin {
    @Override
    public List<String> getJopaScanPackages() {
        return List.of(PersistenceEntity.class.getPackageName());
    }

    @Override
    public List<String> getSpringScanPackages() {
        return List.of(); // Core is the Spring application, no need for additional scan
    }
}
