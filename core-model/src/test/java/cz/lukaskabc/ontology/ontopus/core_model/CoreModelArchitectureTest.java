package cz.lukaskabc.ontology.ontopus.core_model;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

public class CoreModelArchitectureTest {

    private final JavaClasses ontopusClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_PACKAGE_INFOS)
            .importPackages("cz.lukaskabc.ontology.ontopus")
            .as("Ontopus classes");

    @Test
    void coreModelClassesShouldBePublic() {
        classes()
                .that()
                .areNotAnonymousClasses() // exclude compiler-generated classes
                .should()
                .bePublic()
                .check(ontopusClasses);
    }

    @Test
    void coreModelModuleDoesNotDependOnOtherModules() {
        noClasses()
                .should()
                .transitivelyDependOnClassesThat(resideInNonCoreModelPackage())
                .check(ontopusClasses);
    }

    @Test
    void layersShouldBeStrictlyRespected() {
        layeredArchitecture()
                .consideringAllDependencies()
                // spotless:off to keep the call inline
                // layer definition
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..persistence.repository..")
                .layer("Dao").definedBy("..persistence.dao..")
                // rules
                .whereLayer("Service").mayNotBeAccessedByAnyLayer()
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .whereLayer("Dao").mayOnlyBeAccessedByLayers("Repository")
                // spotless:on
                // assertation
                .check(ontopusClasses);
    }

    @Test
    void noPersistenceCycles() {
        slices().matching("..persistence.(**)").should().beFreeOfCycles().check(ontopusClasses);
    }

    @Test
    void noServiceCycles() {
        slices().matching("..service.(**)").should().beFreeOfCycles().check(ontopusClasses);
    }

    @Test
    void ontopusClassesAreNotEmpty() {
        assertFalse(ontopusClasses.isEmpty());
        assertTrue(ontopusClasses.contain(CoreModel.class));
    }

    DescribedPredicate<JavaClass> resideInNonCoreModelPackage() {
        return resideInAnyPackage("cz.lukaskabc.ontology.ontopus..")
                .and(not(resideInAnyPackage("..ontopus.core_model..")));
    }
}
