package cz.lukaskabc.ontology.ontopus.core_model.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

public class OntopusConfigValidator implements ConstraintValidator<ValidOntopusConfig, OntopusConfig> {
    private boolean ensureDcatUriIsNotSystemUriPrefix(OntopusConfig config, ConstraintValidatorContext context) {
        final String systemUri = config.getSystemUri().toString().replaceAll("[#/]+$", "");
        final String dcatUri = config.getDcatCatalog().getBaseUri().toString().replaceAll("[#/]+$", "");

        if (systemUri.startsWith(dcatUri)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("System URI must not be a prefix of the DCAT URI")
                    .addPropertyNode("dcatCatalog.baseUri")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    /**
     * Implements the validation logic. The state of {@code value} must not be altered.
     *
     * <p>This method can be accessed concurrently, thread-safety must be ensured by the implementation.
     *
     * @param value object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(OntopusConfig value, ConstraintValidatorContext context) {
        Objects.requireNonNull(value, "OntopusConfig must not be null");
        return ensureDcatUriIsNotSystemUriPrefix(value, context);
    }
}
