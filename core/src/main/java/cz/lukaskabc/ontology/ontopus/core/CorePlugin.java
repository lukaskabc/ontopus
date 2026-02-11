package cz.lukaskabc.ontology.ontopus.core;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.core_model.CoreModel;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class CorePlugin implements Plugin {
    @Override
    public List<String> getJopaScanPackages() {
        return List.of(CoreModel.class.getPackageName());
    }

    @Override
    public List<String> getSpringScanPackages() {
        return List.of(CoreModel.class.getPackageName());
    }
}
