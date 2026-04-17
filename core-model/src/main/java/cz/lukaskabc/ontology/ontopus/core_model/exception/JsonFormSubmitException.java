package cz.lukaskabc.ontology.ontopus.core_model.exception;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

/** Indicates a failure of JSON form submission */
public class JsonFormSubmitException extends OntopusCheckedException {
    public static JsonFormSubmitExceptionBuilderStages.ErrorTypeBuildStage builder() {
        return JsonFormSubmitExceptionBuilderStages.start().statusCode(HttpStatus.BAD_REQUEST);
    }

    public static JsonFormSubmitException missingValue(String paramName) {
        return JsonFormSubmitException.builder()
                .errorType(Vocabulary.u_i_form_submit)
                .internalMessage("Form data are missing value for " + paramName)
                .titleMessageCode("ontopus.core.error.form.missingValue.title")
                .detailMessageArguments(new Object[] {paramName})
                .detailMessageCode("ontopus.core.error.form.missingValue.detail")
                .build();
    }

    @org.immutables.builder.Builder.Constructor
    public JsonFormSubmitException(
            HttpStatusCode statusCode,
            URI errorType,
            String internalMessage,
            String titleMessageCode,
            @Nullable Throwable cause,
            @Nullable String detailMessageCode,
            Object[] detailMessageArguments) {
        super(
                statusCode,
                errorType,
                internalMessage,
                titleMessageCode,
                cause,
                detailMessageCode,
                detailMessageArguments);
    }
}
