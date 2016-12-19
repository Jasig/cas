package org.apereo.cas.support.saml.web.idp.profile.builders.enc;

import org.apache.commons.lang3.tuple.Pair;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.support.saml.services.idp.metadata.SamlRegisteredServiceServiceProviderMetadataFacade;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is {@link SamlAttributeEncoder}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class SamlAttributeEncoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SamlAttributeEncoder.class);

    /**
     * Encode and transform attributes.
     *
     * @param authnRequest the authn request
     * @param attributes   the attributes
     * @param service      the service
     * @param adaptor      the service provider facade
     * @return the map
     */
    public Map<String, Object> encode(final AuthnRequest authnRequest,
                                      final Map<String, Object> attributes,
                                      final SamlRegisteredService service,
                                      final SamlRegisteredServiceServiceProviderMetadataFacade adaptor) {
        final Map<String, Object> finalAttributes = new HashMap<>(attributes);

        transformUniformResourceNames(finalAttributes);

        return finalAttributes;
    }

    private static void transformUniformResourceNames(final Map<String, Object> attributes) {
        final Set<Pair<String, Object>> attrs = attributes.keySet().stream()
                .filter(s -> s.toLowerCase().startsWith("urn_"))
                .map(s -> Pair.of(s.replace('_', ':'), attributes.get(s)))
                .collect(Collectors.toSet());
        if (!attrs.isEmpty()) {
            LOGGER.debug("Found {} URN attribute(s) that will be transformed.");
            attributes.entrySet().removeIf(s -> s.getKey().startsWith("urn_"));
            attrs.forEach(p -> {
                LOGGER.debug("Transformed attribute name to be {}", p.getKey());
                attributes.put(p.getKey(), p.getValue());
            });
        }
    }
}
