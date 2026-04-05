package cz.lukaskabc.ontology.ontopus.core_model.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.lang.Contract;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.regex.Pattern;

public class StringUtils extends org.springframework.util.StringUtils {
    private static final char[] SANITIZATION_ALLOWED_CHARACTERS = new char[] {'-', '_'};

    public static final String MARKDOWN_SPECIAL_CHARACTERS = "\\`*_{}[]()#+-.!";

    private static final Pattern SLASH_PLUS = Pattern.compile("/+");

    private static final Pattern MINUS_PLUS = Pattern.compile("-+");

    /** Decodes the given URL with {@link URLDecoder} and transforms the scheme to HTTP if HTTPS is used. */
    public static String decodeUrlAsHttp(URI uri, Charset charset) {
        UriComponents components = UriComponentsBuilder.fromUri(uri).build();
        String uriString = components.toUriString();
        if (components.getScheme() != null && components.getScheme().equalsIgnoreCase("https")) {
            uriString = UriComponentsBuilder.fromUri(components.toUri())
                    .scheme("http")
                    .toUriString();
        }
        return URLDecoder.decode(uriString, charset);
    }

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

    /**
     * Sanitizes the uri by replacing all slashes with {@code -} and deduplicating multiple {@code -} characters into a
     * single one, then applying the default sanitization.
     *
     * @see #sanitize(String)
     * @param uri the URI to sanitize as an URI component
     * @return sanitized URI component
     */
    public static String sanitizeUriAsComponent(String uri) {
        final String formatted = SLASH_PLUS.matcher(uri).replaceAll("-");
        final String deduplicated = MINUS_PLUS.matcher(formatted).replaceAll("-");

        return StringUtils.sanitize(deduplicated);
    }

    /**
     * Removes any number of trailing slashes from the value. If the value does not contain a trailing slash or is
     * empty, the same object is returned.
     *
     * @param value the value to trim
     * @return The {@code value} if its null, empty or does not contain a trailing slash, or a new string without
     *     trailing slashes otherwise.
     */
    @Contract("null -> param1; !null -> !null")
    public static @Nullable String withoutTrailingSlash(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        int i = value.length() - 1;
        while (i >= 0 && value.charAt(i) == '/') {
            i--;
        }
        if (i == value.length() - 1) {
            return value;
        }
        return i < 0 ? "" : value.substring(0, i + 1);
    }

    /**
     * Removes trailing slashes from the URI
     *
     * @param uri the uri to trim
     * @return a new uri without trailing slashes or the {@code uri} object if there is no trailing slash
     */
    @SuppressWarnings("StringEquality")
    public static URI withoutTrailingSlash(URI uri) {
        final String str = uri.toString();
        final String withoutTrailingSlash = withoutTrailingSlash(str);
        // withoutTrailingSlash guarantees to return the param1 if no change is required
        if (str == withoutTrailingSlash) {
            return uri;
        }
        try {
            return new URI(withoutTrailingSlash);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URI: " + uri, e);
        }
    }

    private StringUtils() {
        throw new AssertionError();
    }
}
