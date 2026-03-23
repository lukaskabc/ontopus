package cz.lukaskabc.ontology.ontopus.api.exception;

/** Indicates a failure of JSON form submission */
public class JsonFormSubmitException extends Exception {
    private final String translationKey;

    public JsonFormSubmitException(String message, String translationKey, Throwable cause) {
        super(message, cause);
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
