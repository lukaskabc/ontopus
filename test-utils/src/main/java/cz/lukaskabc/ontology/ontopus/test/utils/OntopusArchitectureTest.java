package cz.lukaskabc.ontology.ontopus.test.utils;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AnalyzeClasses(
        packages = {"cz.lukaskabc.ontology.ontopus", "cz.cvut.kbss.jopa.model"},
        importOptions = ImportOption.Predefined.DoNotIncludePackageInfos.class)
public @interface OntopusArchitectureTest {}
