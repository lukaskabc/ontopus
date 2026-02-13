package cz.lukaskabc.ontology.ontopus.core.rest.response;

import java.net.URI;

public class DatasetResponse extends ResourceResponse {
    private URI series;

    public URI getSeries() {
        return series;
    }

    public void setSeries(URI series) {
        this.series = series;
    }
}
