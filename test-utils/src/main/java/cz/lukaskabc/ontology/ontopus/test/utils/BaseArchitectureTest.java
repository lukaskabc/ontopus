package cz.lukaskabc.ontology.ontopus.test.utils;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.junit.jupiter.api.Assertions;

public abstract class BaseArchitectureTest {
    @ArchTest
    void classesAreNotEmpty(JavaClasses classes) {
        Assertions.assertFalse(classes.isEmpty());
    }
}
