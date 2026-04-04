package cz.lukaskabc.ontology.ontopus.core.service.content_negotiation;

import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Component
public class ContentNegotiationResolver {

    private static final Comparator<ControllerDescription> CONTROLLER_CLASSNAME_COMPARATOR =
            Comparator.comparing(ControllerDescription::getClassName);

    /**
     * Constructs controller candidate with the most specific media type supported by the controller that is compatible
     * with the requested type.
     *
     * @param requestedType the requested media type
     * @param controller the controller
     * @return the controller candidate or {@code null} if the controller is not able to produce any media compatible
     *     with the requested type.
     */
    @Nullable private ControllerCandidate constructCandidate(MediaType requestedType, ControllerDescription controller) {
        ControllerCandidate candidate = constructCandidateMatching(requestedType, controller);
        if (candidate != null) {
            return candidate;
        }

        final MediaType bestCompatibleType = findMostSpecific(requestedType, controller);
        if (bestCompatibleType != null) {
            return new ControllerCandidate(bestCompatibleType, controller);
        }

        return null;
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
    @Nullable private ControllerCandidate constructCandidateMatching(MediaType requestedType, ControllerDescription controller) {
        if (controller.getSupportedMediaTypes().contains(requestedType)) {
            return new ControllerCandidate(requestedType, controller);
        }
        return null;
    }

    /**
     * Finds the most specific media type supported by the controller that is compatible with the requested type.
     *
     * @param requestedType the requested media type
     * @param controller the controller
     * @return the most specific media type supported by the controller that is compatible with the requested type or
     *     {@code null} if the controller is not able to produce any media compatible with the requested type.
     */
    @Nullable private MediaType findMostSpecific(MediaType requestedType, ControllerDescription controller) {
        List<MediaType> compatibleTypes =
                new ArrayList<>(controller.getSupportedMediaTypes().size());
        for (MediaType supportedType : controller.getSupportedMediaTypes()) {
            if (supportedType.isCompatibleWith(requestedType)) {
                compatibleTypes.add(supportedType);
            }
        }
        if (compatibleTypes.isEmpty()) {
            return null;
        }
        MimeTypeUtils.sortBySpecificity(compatibleTypes);
        assert compatibleTypes.getFirst().isMoreSpecific(compatibleTypes.getLast());
        return compatibleTypes.getFirst();
    }

    /**
     * Maps the requested media type to supported controllers.
     *
     * @param requestedType requested media type
     * @param controllers available controllers
     */
    private Stream<ControllerCandidate> resolveCandidates(
            MediaType requestedType, Collection<ControllerDescription> controllers) {
        return controllers.stream()
                .map(controller -> constructCandidate(requestedType, controller))
                .filter(Objects::nonNull);
    }

    /**
     * Maps requested media types to the provided controllers that are capable of producing the media type.
     *
     * @param requestedTypes ordered array of requested media types
     * @param controllers available controllers
     */
    private Stream<ControllerCandidate> resolveCandidates(
            MediaType[] requestedTypes, Collection<ControllerDescription> controllers) {
        return Arrays.stream(requestedTypes).flatMap(requestedType -> resolveCandidates(requestedType, controllers));
    }

    /**
     * Finds the best suitable controller capable of producing one of the requested media types.
     *
     * @param requestedTypes ordered array of requested types
     * @param controllers available controllers
     * @return the controller and chosen media type
     */
    public Optional<ControllerCandidate> resolveController(
            MediaType[] requestedTypes, Collection<ControllerDescription> controllers) {
        List<ControllerDescription> controllerList = new ArrayList<>(controllers);
        // sorting controllers to keep the results stable
        controllerList.sort(CONTROLLER_CLASSNAME_COMPARATOR);
        AtomicReference<@Nullable ControllerCandidate> bestCandidate = new AtomicReference<>();
        resolveCandidates(requestedTypes, controllerList).forEach(candidate -> {
            final ControllerCandidate currentBest = bestCandidate.get();
            if (currentBest == null || candidate.mediaType().isMoreSpecific(currentBest.mediaType())) {
                bestCandidate.set(candidate);
            }
        });

        return Optional.ofNullable(bestCandidate.get());
    }
}
