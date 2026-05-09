package cz.lukaskabc.ontology.ontopus.tests.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.junit.jupiter.api.Assertions;

/** Base class for architecture tests ensuring that the set of classes under test is not empty */
public abstract class BaseArchitectureTest {
    @ArchTest
    void classesAreNotEmpty(JavaClasses classes) {
        Assertions.assertFalse(classes.isEmpty());
    }
}
