package cz.lukaskabc.ontology.ontopus.api;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.test.utils.BaseArchitectureTest;
import cz.lukaskabc.ontology.ontopus.test.utils.OntopusArchitectureTest;

@SuppressWarnings("unused") // for ArchUnit rule fields
@OntopusArchitectureTest
public class PluginApiArchitectureTest extends BaseArchitectureTest {

    @ArchTest
    static final ArchRule classesImplementingImportProcessingServiceShouldResideInImportPackage = classes()
            .that()
            .resideInAnyPackage("..service.import_process..")
            .should()
            .beAssignableTo(ImportProcessingService.class);

    @ArchTest
    static final ArchRule importProcessingServicesResideInImportPackage = classes()
            .that()
            .areAssignableTo(ImportProcessingService.class)
            .should()
            .resideInAnyPackage("..service.import_process..");

    @ArchTest
    static final ArchRule pluginArchitectureShouldBeStrictlyRespected = layeredArchitecture()
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
            ;
}
