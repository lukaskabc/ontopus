package cz.lukaskabc.ontology.ontopus.core;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.test.utils.BaseArchitectureTest;
import cz.lukaskabc.ontology.ontopus.test.utils.OntopusArchitectureTest;

@SuppressWarnings("unused") // for ArchUnit rule fields
@OntopusArchitectureTest
public class CoreArchitectureTest extends BaseArchitectureTest {

    @ArchTest
    static final ArchRule initializationServicesShouldResideInInitPackage =
            classes().that().implement(InitializationService.class).should().resideInAPackage("..service.init..");

    @ArchTest
    static final ArchRule orderedImportServicesResidesInOrderedPackage = classes()
            .that()
            .implement(OrderedImportPipelineService.class)
            .should()
            .resideInAPackage("..import_process.ordered..");

    @ArchTest
    static final ArchRule pluginArchitectureShouldBeStrictlyRespected = layeredArchitecture()
            .consideringAllDependencies()
            .withOptionalLayers(true)
            // spotless:off to keep the call inline
            // layer definition
            .layer("Core").definedBy("..ontopus.core..")
            .layer("CoreModel").definedBy("..ontopus.core_model..")
            .layer("API").definedBy("..ontopus.api..")
            .layer("Plugin").definedBy("..plugin..")
            // rules
            .whereLayer("Plugin").mayNotBeAccessedByAnyLayer()
            .whereLayer("Core").mayOnlyBeAccessedByLayers("Plugin");
            // spotless:on

    @ArchTest
    static final ArchRule layeredArchitectureShouldBeStrictlyRespected = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            // spotless:off to keep the call inline
            // layer definition
            .layer("Rest").definedBy("..core.rest..")
            .layer("Import process service").definedBy("..core.import_process..")
            .layer("Service").definedBy("..ontopus.core.service..", "..core_model.service..")
            .layer("Repository").definedBy("..persistence.repository..")
            .layer("Dao").definedBy("..persistence.dao..")
            // rules
            .whereLayer("Rest").mayNotBeAccessedByAnyLayer()
            .whereLayer("Import process service").mayOnlyBeAccessedByLayers("Rest")
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Import process service", "Rest")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
            .whereLayer("Dao").mayOnlyBeAccessedByLayers("Repository");
            // spotless:on
}
