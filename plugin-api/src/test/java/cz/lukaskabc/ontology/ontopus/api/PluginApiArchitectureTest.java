package cz.lukaskabc.ontology.ontopus.api;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

public class PluginApiArchitectureTest {

    private final JavaClasses ontopusClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_PACKAGE_INFOS)
            .importPackages("cz.lukaskabc.ontology.ontopus")
            .as("Ontopus classes");

    @Test
    void pluginArchitectureShouldBeStrictlyRespected() {
        layeredArchitecture()
                .consideringAllDependencies()
                // spotless:off to keep the call inline
            // layer definition
            .layer("Core").definedBy("..ontopus.core..")
            .layer("CoreModel").definedBy("..ontopus.core_model..")
            .layer("API").definedBy("..ontopus.api..")
            .layer("Plugin").definedBy("..plugin..")
            .withOptionalLayers(true) // Core and Plugin layers should be empty
            // rules
            .whereLayer("Plugin").mayNotBeAccessedByAnyLayer()
            .whereLayer("Core").mayOnlyBeAccessedByLayers("Plugin")
            // spotless:on
                // assertation
                .check(ontopusClasses);
    }
}
