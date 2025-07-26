package cz.lukaskabc.ontology.ontopus.core;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import java.util.List;
import org.jspecify.annotations.NonNull;

public class CorePlugin implements Plugin {
    @Override
    public @NonNull List<String> getJopaScanPackages() {
        return List.of(PersistenceEntity.class.getPackageName());
    }

    @Override
    public @NonNull List<String> getSpringScanPackages() {
        return List.of(); // Core is the Spring application, no need for additional scan
    }
}
