package cz.lukaskabc.ontology.ontopus.api.util;

import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;

public interface OptionsEntry {
    /**
     * Provides JSON form that will be shown to the user
     *
     * @return the JSON form
     */
    JsonForm getForm();

    /**
     * A unique identifier of the entry
     *
     * @return the unique identifier
     * @implSpec The identifier should be constant for the entry and should not change between application restarts. The
     *     identifier must be unique.
     */
    default String getIdentifier() {
        return this.getClass().getName();
    }

    /**
     * Label of the menu entry.
     *
     * @return i18n key for the translation of the menu entry.
     */
    String getLabel();
}
