package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import org.jspecify.annotations.NonNull;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;

public class StreamingResponseBodyAdapter implements StreamingResponseBody {
    private final cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody delegate;

    public StreamingResponseBodyAdapter(cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody delegate) {
        this.delegate = delegate;
    }

    @Override
    public void writeTo(@NonNull OutputStream outputStream) throws IOException {
        delegate.writeTo(outputStream);
    }
}
