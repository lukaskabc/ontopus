package cz.lukaskabc.ontology.ontopus.core_model;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    private DescribedPredicate<JavaClass> areNotPackageInfoFile() {
        return DescribedPredicate.describe(
                "are not packge-info file",
                javaClass -> !javaClass.getSimpleName().equals("package-info"));
    }

    private JavaClasses coreModelClasses() {
        return new ClassFileImporter()
                .importPackages(coreModelPackages())
                .that(areNotPackageInfoFile())
                .as("Core model classes");
    }

    @Test
    void coreModelClassesShouldBePublic() {
        classes().should().bePublic().check(coreModelClasses());
    }

    @Test
    void coreModelModuleDoesNotDependOnOtherModules() {
        noClasses()
                .should()
                .transitivelyDependOnClassesThat(resideInNonCoreModelPackage())
                .check(coreModelClasses());
    }

    private String coreModelPackages() {
        return "cz.lukaskabc.ontology.ontopus.core_model..";
    }

    DescribedPredicate<JavaClass> resideInNonCoreModelPackage() {
        return resideInAnyPackage("cz.lukaskabc.ontology.ontopus..").and(not(resideInAnyPackage(coreModelPackages())));
    }
}
