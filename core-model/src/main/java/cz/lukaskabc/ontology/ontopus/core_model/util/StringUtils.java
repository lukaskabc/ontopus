package cz.lukaskabc.ontology.ontopus.core_model.util;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Objects;

public class StringUtils extends org.springframework.util.StringUtils {
    private static final char[] SANITIZATION_ALLOWED_CHARACTERS = new char[] {'-', '_'};

    /**
     * Filters out all characters from the input string that are not letters, digits, or one of the allowed special
     * characters.
     *
     * @param input the string to sanitize
     * @return a sanitized version of the input string
     * @see #SANITIZATION_ALLOWED_CHARACTERS
     * @throws NullPointerException if the input string is null
     * @throws IllegalArgumentException if the sanitized string is empty
     * @see #sanitize(String, char[])
     */
    public static String sanitize(String input) {
        return sanitize(input, SANITIZATION_ALLOWED_CHARACTERS);
    }

    /**
     * Filters out all characters from the input string that are not letters, digits, or one of the allowed special
     * characters.
     *
     * @param input the string to sanitize
     * @param allowedCharacters an array of additional characters to allow in the sanitized string
     * @return a sanitized version of the input string
     * @throws NullPointerException if the input string is null
     * @throws IllegalArgumentException if the sanitized string is empty
     * @see #sanitize(String, char[])
     */
    public static String sanitize(String input, char[] allowedCharacters) {
        Objects.requireNonNull(input, "Cannot sanitize null string");
        StringBuilder sanitized = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c) || ArrayUtils.contains(allowedCharacters, c)) {
                sanitized.append(c);
            } else if (Character.isWhitespace(c)) {
                sanitized.append("_");
            }
        }
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("Sanitized string cannot be empty");
        }
        return sanitized.toString();
    }

    private StringUtils() {
        throw new AssertionError();
    }
}
