package cz.lukaskabc.ontology.ontopus.core;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;
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
            .layer("Rest").definedBy("..core.rest.controller..")
            .layer("Import process service").definedBy("..core.import_process..")
            .layer("Service").definedBy("..ontopus.core.service..")
            .layer("Model Service").definedBy("..core_model.service..")
            .layer("Repository").definedBy("..persistence.repository..")
            .layer("Dao").definedBy("..persistence.dao..")
            // rules
            .whereLayer("Rest").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Rest")
            .whereLayer("Import process service").mayOnlyBeAccessedByLayers("Service")
            .whereLayer("Model Service").mayOnlyBeAccessedByLayers("Service", "Import process service", "Rest")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Model Service")
            .whereLayer("Dao").mayOnlyBeAccessedByLayers("Repository");
            // spotless:on

    /** Each service class is assigned to its own slice */
    static SliceAssignment coreServiceSlices = new SliceAssignment() {
        @Override
        public String getDescription() {
            return "each individual class";
        }

        @Override
        public SliceIdentifier getIdentifierOf(JavaClass javaClass) {
            // Target only the classes within the specific package
            if (javaClass.getPackageName().startsWith("cz.lukaskabc.ontology.ontopus.core.service")) {
                // Assign each class to its own slice using its Simple Name
                return SliceIdentifier.of(javaClass.getSimpleName());
            }
            // Ignore classes outside this package
            return SliceIdentifier.ignore();
        }
    };

    @ArchTest
    static final ArchRule noServiceCyclicDependencies =
            slices().assignedFrom(coreServiceSlices).should().beFreeOfCycles();

    @ArchTest
    static final ArchRule entityManagerIsAccessedOnlyInDaoAndIdentifierGenerators = classes()
            .that()
            .areAssignableTo("cz.cvut.kbss.jopa.model.EntityManager")
            .or()
            .haveFullyQualifiedName("cz.cvut.kbss.jopa.model.EntityManager")
            .should()
            .onlyBeAccessed()
            .byAnyPackage(
                    "cz.cvut.kbss.jopa..",
                    "..persistence.dao..",
                    "..persistence.identifier..",
                    // TODO access from property mapper is not nice:
                    "..api.util..");
}
