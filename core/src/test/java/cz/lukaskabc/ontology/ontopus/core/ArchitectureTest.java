package cz.lukaskabc.ontology.ontopus.core;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.conditions.ArchConditions.be;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import cz.lukaskabc.ontology.ontopus.core.rest.ImportController;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {
    private JavaClasses coreClasses() {
        return new ClassFileImporter().importPackages(corePackages()).as("Core classes");
    }

    @Test
    void coreClassesAreNotEmpty() {
        assertFalse(coreClasses().isEmpty());
        assertTrue(coreClasses().contain(OntoPuSApplication.class));
        assertTrue(coreClasses().contain(ImportController.class));
    }

    @Test
    void coreModuleDoesNotDependOnPlugins() {
        noClasses()
                .should()
                .transitivelyDependOnClassesThat(resideInPluginPackage())
                .andShould(be(resideInPluginPackage()))
                .check(coreClasses());
    }

    private String corePackages() {
        return "cz.lukaskabc.ontology.ontopus.core..";
    }

    private DescribedPredicate<JavaClass> resideInPluginPackage() {
        return resideInAnyPackage("..plugin..");
    }
}
