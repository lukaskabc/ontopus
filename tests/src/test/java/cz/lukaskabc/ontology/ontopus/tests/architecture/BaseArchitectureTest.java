package cz.lukaskabc.ontology.ontopus.tests.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

/** Base class for architecture tests ensuring that the set of classes under test is not empty */
public abstract class BaseArchitectureTest {
    private static final Logger log = LogManager.getLogger(BaseArchitectureTest.class);

    @ArchTest
    void classesAreNotEmpty(JavaClasses classes) {
        Assertions.assertFalse(classes.isEmpty());
        log.info("Testing {} Java classes", classes.size());
        // log.trace("Packages under test:");
        // classes.stream()
        // .map(JavaClass::getPackage)
        // .distinct()
        // .map(JavaPackage::getName)
        // .forEach(log::trace);
    }
}
