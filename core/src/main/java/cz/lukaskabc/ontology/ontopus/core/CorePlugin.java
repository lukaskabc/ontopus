package cz.lukaskabc.ontology.ontopus.core;

import cz.lukaskabc.ontology.ontopus.api.Plugin;
import cz.lukaskabc.ontology.ontopus.core_model.CoreModel;
import cz.lukaskabc.ontology.ontopus.core_model.model.converter.SerializableImportProcessContextConverter;
import cz.lukaskabc.ontology.ontopus.core_model.model.converter.id.AbstractEntityIdentifierConverter;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class CorePlugin implements Plugin {
    @Override
    public List<String> getJopaScanPackages() {
        return List.of(
                CoreModel.class.getPackageName(),
                SerializableImportProcessContextConverter.class.getPackageName(),
                AbstractEntityIdentifierConverter.class.getPackageName());
    }

    @Override
    public List<String> getSpringScanPackages() {
        return List.of(CoreModel.class.getPackageName());
    }
}
