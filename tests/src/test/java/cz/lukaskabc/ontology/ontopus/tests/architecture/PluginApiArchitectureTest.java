package cz.lukaskabc.ontology.ontopus.tests.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;

@SuppressWarnings("unused") // for ArchUnit rule fields
@OntopusArchitectureTest
public class PluginApiArchitectureTest extends BaseArchitectureTest {
    @ArchTest
    static final ArchRule orderedImportServicesResidesInOrderedPackage = classes()
            .that()
            .implement(OrderedImportPipelineService.class)
            .should()
            .resideInAPackage("..import_process.ordered..");

    @ArchTest
    static final ArchRule orderedImportServicesImplementOrderedImportService = classes()
            .that()
            .resideInAPackage("..import_process.ordered..")
            .should()
            .implement(OrderedImportPipelineService.class);

    @ArchTest
    static final ArchRule importProcessingServicesResideInImportPackage = classes()
            .that()
            .areAssignableTo(ImportProcessingService.class)
            .should()
            .resideInAnyPackage("..import_process..");
}
