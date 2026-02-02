package cz.lukaskabc.ontology.ontopus.core.validation;

import cz.cvut.kbss.jopa.model.MultilingualString;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotEmpty;

public class NotEmptyValidatorForMultilingualString implements ConstraintValidator<NotEmpty, MultilingualString> {
    @Override
    public boolean isValid(MultilingualString value, ConstraintValidatorContext context) {
        return !value.isEmpty();
    }
}
