package cz.lukaskabc.ontology.ontopus.tests.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.junit.AnalyzeClasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AnalyzeClasses(
        packages = {OntopusArchitectureTest.ONTOPUS_PACKAGES, OntopusArchitectureTest.JOPA_MODEL_PACKAGES},
        importOptions = OntopusArchitectureTest.ImportOptions.class)
public @interface OntopusArchitectureTest {
    public static final String ONTOPUS_PACKAGES = "cz.lukaskabc.ontology.ontopus..";
    public static final String JOPA_MODEL_PACKAGES = "cz.cvut.kbss.jopa.model..";

    public class ImportOptions implements ImportOption {

        @Override
        public boolean includes(Location location) {
            return Predefined.DO_NOT_INCLUDE_TESTS.includes(location)
                    && Predefined.DO_NOT_INCLUDE_PACKAGE_INFOS.includes(location);
        }
    }
}
