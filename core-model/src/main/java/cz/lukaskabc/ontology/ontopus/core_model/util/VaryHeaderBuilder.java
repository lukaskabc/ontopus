package cz.lukaskabc.ontology.ontopus.core_model.util;

import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class VaryHeaderBuilder {
    public static VaryHeaderBuilder withOrigin() {
        return new VaryHeaderBuilder().addOrigin();
    }

    private final Set<String> headers = new LinkedHashSet<>();

    public VaryHeaderBuilder addAccept() {
        addHeader(HttpHeaders.ACCEPT);
        return this;
    }

    public VaryHeaderBuilder addAcceptCharset() {
        addHeader(HttpHeaders.ACCEPT_CHARSET);
        return this;
    }

    public VaryHeaderBuilder addAcceptEncoding() {
        addHeader(HttpHeaders.ACCEPT_ENCODING);
        return this;
    }

    public VaryHeaderBuilder addAcceptLanguage() {
        addHeader(HttpHeaders.ACCEPT_LANGUAGE);
        return this;
    }

    /**
     * Adds the specified Header name
     *
     * @param name the name of the HTTP header
     * @return true if the header was not added before
     */
    public VaryHeaderBuilder addHeader(String name) {
        headers.add(name);
        return this;
    }

    public VaryHeaderBuilder addOrigin() {
        addHeader(HttpHeaders.ORIGIN);
        return this;
    }

    public Set<String> getHeaders() {
        return Collections.unmodifiableSet(this.headers);
    }

    public void setToHeaders(HttpHeaders headers) {
        headers.setVary(new ArrayList<>(this.headers));
    }
}
