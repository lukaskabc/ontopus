package cz.lukaskabc.ontology.ontopus.core.service.content_negotiation;

import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.Controller;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Component
public class ContentNegotiationResolver {
    @Nullable private ControllerCandidate constructCandidate(MediaType requestedType, Controller controller) {
        ControllerCandidate candidate = constructCandidateMatching(requestedType, controller);
        if (candidate != null) {
            return candidate;
        }
        return constructCandidateBestCompatible(requestedType, controller);
    }

    @Nullable private ControllerCandidate constructCandidateBestCompatible(MediaType requestedType, Controller controller) {
        List<MediaType> compatibleTypes =
                new ArrayList<>(controller.getSupportedMediaTypes().size());
        controller.getSupportedMediaTypes().forEach(supportedType -> {
            if (supportedType.isCompatibleWith(requestedType)) {
                compatibleTypes.add(supportedType);
            }
        });
        if (compatibleTypes.isEmpty()) {
            return null;
        }
        MimeTypeUtils.sortBySpecificity(compatibleTypes);
        assert compatibleTypes.getFirst().isMoreSpecific(compatibleTypes.getLast());
        return new ControllerCandidate(compatibleTypes.getFirst(), controller);
    }

    /**
     * Checks whether the controller supports the exact requested media type. If so, constructs a candidate for this
     * controller and the requested media type. Otherwise, returns null.
     *
     * @param requestedType the media type requested by the client
     * @param controller the controller for which the candidate should be constructed
     * @return a candidate for the given controller and media type if the controller supports the exact media type, null
     *     otherwise
     */
    @Nullable private ControllerCandidate constructCandidateMatching(MediaType requestedType, Controller controller) {
        if (controller.getSupportedMediaTypes().contains(requestedType)) {
            return new ControllerCandidate(requestedType, controller);
        }
        return null;
    }

    private Stream<@Nullable ControllerCandidate> resolveCandidates(
            MediaType requestedTypes, Collection<Controller> controllers) {
        return controllers.stream()
                .<@Nullable ControllerCandidate>map(controller -> constructCandidate(requestedTypes, controller));
    }

    private Stream<ControllerCandidate> resolveCandidates(
            MediaType[] requestedTypes, Collection<Controller> controllers) {
        return Arrays.stream(requestedTypes)
                .flatMap(requestedType -> resolveCandidates(requestedType, controllers))
                .filter(Objects::nonNull);
    }

    public Optional<ControllerCandidate> resolveController(
            MediaType[] requestedTypes, Collection<Controller> controllers) {
        AtomicReference<ControllerCandidate> bestCandidate = new AtomicReference<>();
        resolveCandidates(requestedTypes, controllers).forEach(candidate -> {
            final ControllerCandidate currentBest = bestCandidate.get();
            if (currentBest == null || candidate.mediaType().isMoreSpecific(currentBest.mediaType())) {
                bestCandidate.set(candidate);
            }
        });

        return Optional.ofNullable(bestCandidate.get());
    }
}
