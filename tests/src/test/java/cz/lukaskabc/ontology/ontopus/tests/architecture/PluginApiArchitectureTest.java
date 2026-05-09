package cz.lukaskabc.ontology.ontopus.tests.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;

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
}
