package cz.lukaskabc.ontology.ontopus.core_model.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

public class StringUtils extends org.springframework.util.StringUtils {
    private static final char[] SANITIZATION_ALLOWED_CHARACTERS = new char[] {'-', '_'};

    public static final String MARKDOWN_SPECIAL_CHARACTERS = "\\`*_{}[]()#+-.!";

    public static String escapeMarkdown(String input) {
        final StringBuilder escaped = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (MARKDOWN_SPECIAL_CHARACTERS.indexOf(c) > -1) {
                escaped.append('\\');
            }
            escaped.append(c);
        }
        return escaped.toString();
    }

    public static String randomString(int length) {
        return RandomStringUtils.secure().next(length, true, true);
    }

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

    private static final Pattern SLASH_PLUS = Pattern.compile("/+");
    private static final Pattern MINUS_PLUS = Pattern.compile("-+");

    public static String sanitizeUriAsComponent(String uri) {
            final String formatted = SLASH_PLUS.matcher(uri).replaceAll("-");
            final String deduplicated = MINUS_PLUS.matcher(formatted).replaceAll("-");;
            return StringUtils.sanitize(deduplicated);

    }

    private StringUtils() {
        throw new AssertionError();
    }
}
