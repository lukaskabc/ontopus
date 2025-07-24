package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
public abstract class PersistenceEntity {
    @Id(generated = true)
    private URI uri;
}
