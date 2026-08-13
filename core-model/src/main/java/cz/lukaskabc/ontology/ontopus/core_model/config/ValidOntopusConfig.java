package cz.lukaskabc.ontology.ontopus.core_model.config;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Validates {@link OntopusConfig} */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OntopusConfigValidator.class)
public @interface ValidOntopusConfig {
    Class<?>[] groups() default {};

    String message() default "Invalid Ontopus configuration";

    Class<? extends Payload>[] payload() default {};
}
