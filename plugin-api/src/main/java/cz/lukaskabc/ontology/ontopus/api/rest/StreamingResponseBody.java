package cz.lukaskabc.ontology.ontopus.api.rest;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface StreamingResponseBody {
    /**
     * A callback for writing to the response body.
     *
     * @param outputStream the stream for the response body
     * @throws IOException an exception while writing
     */
    void writeTo(OutputStream outputStream) throws IOException;
}
