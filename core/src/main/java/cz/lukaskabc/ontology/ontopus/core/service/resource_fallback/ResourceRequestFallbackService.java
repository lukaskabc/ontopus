package cz.lukaskabc.ontology.ontopus.core.service.resource_fallback;

import cz.lukaskabc.ontology.ontopus.core_model.exception.InitializationException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.function.Function;

@Service
public class ResourceRequestFallbackService {
    private final List<ResourceRequestFallback> fallbacks;

    public ResourceRequestFallbackService(List<ResourceRequestFallback> fallbacks) {
        this.fallbacks = fallbacks;
        if (fallbacks.size() > 5) {
            throw new InitializationException(
                    "More than 5 resource fallbacks registered! This would result in combinatorial explosion!");
        }
    }

    // TODO: ideally the generated resources should be batched and asked for
    // existence to pick the correct one in single database call
    public <R> R withFallback(ResourceURI resourceURI, Function<ResourceURI, R> consumer) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(resourceURI.toURI());
        final int fallbacksCount = fallbacks.size();
        final int masksCount = 1 << fallbacksCount;

        List<Integer> masks = new ArrayList<>(masksCount);
        for (int i = 0; i < masksCount; i++) {
            masks.add(i);
        }
        // prioritize masks with less bits
        masks.sort(Comparator.comparingInt(Integer::bitCount));

        Set<UriComponents> testedUris = new HashSet<>(masksCount);
        for (int mask : masks) {
            UriComponentsBuilder fallbackBuilder = builder.cloneBuilder();

            for (int i = 0; i < fallbacksCount; i++) {
                if ((mask & (1 << i)) != 0) {
                    fallbacks.get(i).accept(fallbackBuilder);
                }
            }

            UriComponents components = fallbackBuilder.build();
            if (testedUris.add(components)) {
                try {
                    return consumer.apply(new ResourceURI(components.toUri()));
                } catch (NotFoundException e) {
                    // err 404, continue with the next fallback
                }
            }
        }

        throw NotFoundException.builder()
                .internalMessage("Resource not found")
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .build();
    }
}
