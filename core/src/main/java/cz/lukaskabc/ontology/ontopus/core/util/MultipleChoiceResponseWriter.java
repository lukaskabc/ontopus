package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

public class MultipleChoiceResponseWriter implements StreamingResponseBody {
    private final Map<String, MediaType> supportedExtensions;
    private final ResourceURI resourceURI;
    private final OntopusConfig.Resource resourceConfig;

    public MultipleChoiceResponseWriter(
            Map<String, MediaType> supportedExtensions, ResourceURI resourceURI, OntopusConfig ontopusConfig) {
        this.supportedExtensions = supportedExtensions;
        this.resourceURI = resourceURI;
        this.resourceConfig = ontopusConfig.getResource();
    }

    private String getDestinationLink() {
        if (resourceConfig.isTrailingSlashFallsBackToNoSlash()) {
            return StringUtils.withoutTrailingSlash(resourceURI.toString());
        }
        return resourceURI.toString();
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        final String baseLink = getDestinationLink() + ".";

        try (PrintWriter writer = new PrintWriter(outputStream)) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head><title>300 Multiple Choices</title></head>");
            writer.println("<body>");
            writer.println("<h1>Multiple Choices</h1>");
            writer.println("<p>The requested resource is available in following formats:</p>");

            writer.println("<ul>");
            supportedExtensions.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        final String ext = entry.getKey();
                        final MediaType type = entry.getValue();
                        writer.write("<li><a href=\"");
                        final String link = baseLink + ext;
                        writer.write(link);
                        writer.write("\">");
                        writer.write(link);
                        writer.write("</a> [");
                        writer.write(type.toString());
                        writer.write("]");
                        writer.println("</li>");
                    });
            writer.println("</ul>");
            writer.println("</body>");
            writer.println("</html>");
        }
    }
}
