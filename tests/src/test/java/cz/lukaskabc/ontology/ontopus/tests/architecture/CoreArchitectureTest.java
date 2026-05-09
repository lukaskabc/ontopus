package cz.lukaskabc.ontology.ontopus.tests.architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@SuppressWarnings("unused") // for ArchUnit rule fields
@OntopusArchitectureTest
public class CoreArchitectureTest extends BaseArchitectureTest {

    @ArchTest
    static final ArchRule layeredArchitectureShouldBeRespected = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            // spotless:off to keep the call inline
            // layer definition
            .layer("Controller").definedBy("..core.rest.controller..")
            .layer("Import process service").definedBy("..core.import_process..")
            .layer("Service").definedBy("..core.service..")
            .layer("Model Service").definedBy("..core_model.service..")
            .layer("Repository").definedBy("..persistence.repository..")
            .layer("Dao").definedBy("..persistence.dao..")
            // rules
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Import process service").mayOnlyBeAccessedByLayers("Service")
            .whereLayer("Model Service").mayOnlyBeAccessedByLayers("Service", "Import process service", "Controller")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Model Service")
            .whereLayer("Dao").mayOnlyBeAccessedByLayers("Repository");
            // spotless:on
}
