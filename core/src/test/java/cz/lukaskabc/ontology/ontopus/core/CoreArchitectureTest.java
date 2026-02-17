package cz.lukaskabc.ontology.ontopus.core;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import cz.lukaskabc.ontology.ontopus.core.rest.ImportController;
import org.junit.jupiter.api.Test;

public class CoreArchitectureTest {

    private final JavaClasses ontopusClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_PACKAGE_INFOS)
            .importPackages("cz.lukaskabc.ontology.ontopus")
            .as("Ontopus classes");

    @Test
    void ontopusClassesAreNotEmpty() {
        assertFalse(ontopusClasses.isEmpty());
        assertTrue(ontopusClasses.contain(OntoPuSApplication.class));
        assertTrue(ontopusClasses.contain(ImportController.class));
    }

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
                // rules
                .whereLayer("Plugin").mayNotBeAccessedByAnyLayer()
                .whereLayer("Core").mayOnlyBeAccessedByLayers("Plugin")
                // spotless:on
                // assertation
                .check(ontopusClasses);
    }
}
