package cz.lukaskabc.ontology.ontopus.core;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core.rest.ImportController;
import org.junit.jupiter.api.Test;

public class CoreArchitectureTest {

    private final JavaClasses ontopusClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_PACKAGE_INFOS)
            .importPackages("cz.lukaskabc.ontology.ontopus")
            .as("Ontopus classes");

    @Test
    void initializationServicesShouldResideInInitPackage() {
        classes()
                .that()
                .implement(InitializationService.class)
                .should()
                .resideInAPackage("..service.init..")
                .check(ontopusClasses);
    }

    @Test
    void ontopusClassesAreNotEmpty() {
        assertFalse(ontopusClasses.isEmpty());
        assertTrue(ontopusClasses.contain(OntoPuSApplication.class));
        assertTrue(ontopusClasses.contain(ImportController.class));
    }

    @Test
    void orderedImportServicesResidesInOrderedPackage() {
        classes()
                .that()
                .implement(OrderedImportPipelineService.class)
                .should()
                .resideInAPackage("..import_process.ordered..")
                .check(ontopusClasses);
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
