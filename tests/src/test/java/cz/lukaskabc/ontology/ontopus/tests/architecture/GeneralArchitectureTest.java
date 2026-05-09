package cz.lukaskabc.ontology.ontopus.tests.architecture;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;
import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import cz.lukaskabc.ontology.ontopus.api.util.PropertyMapper;
import org.apache.logging.log4j.Logger;

/** General architecture rules applied to all ontopus classes */
@SuppressWarnings("unused") // for ArchUnit rule fields
@OntopusArchitectureTest
public class GeneralArchitectureTest extends BaseArchitectureTest {
    @ArchTest
    static final ArchRule initializationServicesShouldResideInInitPackage =
            classes().that().implement(InitializationService.class).should().resideInAPackage("..service.init..");

    @ArchTest
    static final ArchRule pluginArchitectureShouldBeStrictlyRespected = layeredArchitecture()
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
        .ensureAllClassesAreContainedInArchitectureIgnoring(
            OntopusArchitectureTest.JOPA_MODEL_PACKAGES,
            "..ontopus.tests..");
        // spotless:on

    /** Each class is assigned to its own slice */
    static SliceAssignment serviceClassSlices = new SliceAssignment() {
        @Override
        public String getDescription() {
            return "each individual class";
        }

        @Override
        public SliceIdentifier getIdentifierOf(JavaClass javaClass) {
            if (javaClass.isAnonymousClass()
                    || !javaClass.getPackage().getName().contains("service")) {
                return SliceIdentifier.ignore();
            }
            // Target only the classes within the specific package
            if (javaClass.isInnerClass()) {
                // Assign each class to its own slice using its Simple Name
                return SliceIdentifier.of(javaClass.getEnclosingClass().get().getFullName());
            }
            return SliceIdentifier.of(javaClass.getFullName());
        }
    };

    @ArchTest
    static final ArchRule noServiceCyclicDependencies =
            slices().assignedFrom(serviceClassSlices).should().beFreeOfCycles();

    @ArchTest
    static final ArchRule entityManagerIsAccessedOnlyInDaoAndIdentifierGenerators = classes()
            .that()
            .areAssignableTo("cz.cvut.kbss.jopa.model.EntityManager")
            .or()
            .haveFullyQualifiedName("cz.cvut.kbss.jopa.model.EntityManager")
            .should()
            .onlyBeAccessed()
            .byClassesThat(
                    // TODO access from property mapper is not nice:
                    assignableTo(PropertyMapper.class)
                            .or(resideInAnyPackage(
                                    "cz.cvut.kbss.jopa..", "..persistence.dao..", "..persistence.identifier..")));

    /** The only allowed logging package is {@code org.apache.logging.log4j} */
    @ArchTest
    static final ArchRule log4j2ShouldBeTheOnlyUsedLoggingFramework = noClasses()
            .that()
            .resideInAnyPackage("..ontopus..")
            .should()
            .dependOnClassesThat(resideInAnyPackage("..log..", "..logging..", "..slf4j..", "..logback..")
                    .and(resideOutsideOfPackage("org.apache.logging.log4j..")))
            .because("The only allowed logging package is org.apache.logging.log4j (Log4J2)");

    @ArchTest
    static final ArchRule loggerShouldBeConsistentlyNamed = noFields()
            .that()
            .areDeclaredInClassesThat()
            .resideInAnyPackage("..ontopus..")
            .should()
            .haveNameMatching("logger")
            .orShould()
            .haveName("LOG");

    @ArchTest
    static final ArchRule logFieldsShouldBeLog4j2Logger = fields().that()
            .areDeclaredInClassesThat()
            .resideInAnyPackage("..ontopus..")
            .and()
            .haveName("log")
            .should()
            .haveRawType(Logger.class);
}
