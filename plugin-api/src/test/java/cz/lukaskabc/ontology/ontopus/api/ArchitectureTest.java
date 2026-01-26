package cz.lukaskabc.ontology.ontopus.api;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    private JavaClasses apiClasses() {
        return new ClassFileImporter().importPackages(apiPackages()).as("Plugin API classes");
    }

    private String apiPackages() {
        return "cz.lukaskabc.ontology.ontopus.api..";
    }

    @Test
    void pluginApiDoesNotDependOnCore() {
        noClasses()
                .should()
                .transitivelyDependOnClassesThat(resideInAnyPackage("cz.lukaskabc.ontology.ontopus.core.."))
                .check(apiClasses());
    }

    @Test
    void pluginApiDoesNotDependOnPlugin() {
        noClasses()
                .should()
                .transitivelyDependOnClassesThat(resideInAnyPackage("..plugin.."))
                .check(apiClasses());
    }
}
