package cz.lukaskabc.ontology.ontopus.api.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Indicates a failure of JSON form submission */
public class JsonFormSubmitException extends Exception {
    @Nullable private final String translationKey;

    public JsonFormSubmitException(String message) {
        super(message);
        this.translationKey = null;
    }

    public JsonFormSubmitException(String message, @Nullable String translationKey, Throwable cause) {
        super(message, cause);
        this.translationKey = translationKey;
    }

    public JsonFormSubmitRuntimeException asRuntimeException() {
        return new JsonFormSubmitRuntimeException(this);
    }

    @Nullable public String getTranslationKey() {
        return translationKey;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class JsonFormSubmitRuntimeException extends RuntimeException {
        private final JsonFormSubmitException original;

        private JsonFormSubmitRuntimeException(JsonFormSubmitException original) {
            super(original.getMessage(), original);
            this.original = original;
        }

        public @Nullable String getTranslationKey() {
            return original.getTranslationKey();
        }
    }
}
