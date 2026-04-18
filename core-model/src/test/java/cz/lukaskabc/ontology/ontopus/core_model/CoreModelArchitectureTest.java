package cz.lukaskabc.ontology.ontopus.core_model;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import cz.lukaskabc.ontology.ontopus.test.utils.BaseArchitectureTest;
import cz.lukaskabc.ontology.ontopus.test.utils.OntopusArchitectureTest;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unused") // for ArchUnit rule fields
@OntopusArchitectureTest
public class CoreModelArchitectureTest extends BaseArchitectureTest {

    @ArchTest
    static final ArchRule coreModelClassesShouldBePublic = classes()
            .that()
            .areNotAnonymousClasses() // exclude
            // compiler-generated
            // classes
            .and()
            .areNotAnnotatedWith("org.immutables.value.Generated")
            .and()
            .resideInAPackage("..core_model..")
            .should()
            .bePublic();

    static final DescribedPredicate<JavaClass> resideInNonCoreModelPackage = resideInAnyPackage(
                    "cz.lukaskabc.ontology.ontopus..")
            .and(not(resideInAnyPackage("..ontopus.core_model..")));

    @ArchTest
    static final ArchRule coreModelModuleDoesNotDependOnOtherModules =
            noClasses().should().transitivelyDependOnClassesThat(resideInNonCoreModelPackage);

    @ArchTest
    static final ArchRule layersShouldBeStrictlyRespected = layeredArchitecture()
            .consideringAllDependencies()
            // spotless:off to keep the call inline
            // layer definition
            .layer("Service").definedBy("..service..")
            .layer("Repository").definedBy("..persistence.repository..")
            .layer("Identifier generator").definedBy("..persistence.identifier..")
            .layer("Dao").definedBy("..persistence.dao..")
            // rules
            .whereLayer("Service").mayNotBeAccessedByAnyLayer()
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
            .whereLayer("Identifier generator").mayOnlyBeAccessedByLayers("Repository")
            .whereLayer("Dao").mayOnlyBeAccessedByLayers("Repository");
            // spotless:on

    @ArchTest
    static final ArchRule noPersistenceCycles =
            slices().matching("..persistence.(**)").should().beFreeOfCycles();

    @ArchTest
    static final ArchRule noServiceCycles =
            slices().matching("..service.(**)").should().beFreeOfCycles();

    @ArchTest
    static final ArchRule daoMethodsAreNotTransactional = methods()
            .that()
            .areDeclaredInClassesThat()
            .resideInAPackage("..persistence.dao..")
            .should()
            .notBeAnnotatedWith(Transactional.class);

    @ArchTest
    static final ArchRule nonPublicMethodsAreNotTransactional =
            methods().that().areNotPublic().should().notBeAnnotatedWith(Transactional.class);

    @ArchTest
    static final ArchRule publicRepositoryMethodsShouldBeTransactional = methods()
            .that()
            .arePublic()
            .and()
            .areDeclaredInClassesThat()
            .resideInAPackage("..persistence.repository..")
            .should()
            .beAnnotatedWith(Transactional.class);

    @ArchTest
    static final ArchRule entityManagerIsAccessedOnlyInDaoAndIdentifierGenerators = classes()
            .that()
            .areAssignableTo("cz.cvut.kbss.jopa.model.EntityManager")
            .or()
            .haveFullyQualifiedName("cz.cvut.kbss.jopa.model.EntityManager")
            .should()
            .onlyBeAccessed()
            .byAnyPackage("cz.cvut.kbss.jopa..", "..persistence.dao..", "..persistence.identifier..");

    @ArchTest
    static final ArchRule servicesAndPersistenceClassesShouldBeAnnotatedWithComponentAnnotation = classes()
            .that()
            .resideInAnyPackage("..service..", "..persistence..")
            .and()
            .doNotHaveModifier(JavaModifier.ABSTRACT)
            .and()
            .areNotInterfaces()
            .and()
            .areNotEnums()
            .and()
            .areNotAnnotations()
            .and()
            .areNotAnonymousClasses()
            .should()
            .beMetaAnnotatedWith(Component.class);

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
