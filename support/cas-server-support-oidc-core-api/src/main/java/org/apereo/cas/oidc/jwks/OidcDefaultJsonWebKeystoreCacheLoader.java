package org.apereo.cas.oidc.jwks;

import org.apereo.cas.util.LoggingUtils;

import com.github.benmanes.caffeine.cache.CacheLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.PublicJsonWebKey;
import org.springframework.core.io.Resource;

import java.util.Optional;

/**
 * This is {@link OidcDefaultJsonWebKeystoreCacheLoader}.
 * Only attempts to cache the default CAS keystore.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@RequiredArgsConstructor
@Getter
public class OidcDefaultJsonWebKeystoreCacheLoader implements CacheLoader<String, Optional<PublicJsonWebKey>> {
    private final OidcJsonWebKeystoreGeneratorService oidcJsonWebKeystoreGeneratorService;

    @Override
    public Optional<PublicJsonWebKey> load(final String issuer) {
        val jwks = buildJsonWebKeySet();
        if (jwks.isEmpty()) {
            LOGGER.warn("JSON web keystore retrieved is empty for issuer [{}]", issuer);
            return Optional.empty();
        }
        val keySet = jwks.get();
        if (keySet.getJsonWebKeys().isEmpty()) {
            LOGGER.warn("JSON web keystore retrieved [{}] contains no JSON web keys", keySet);
            return Optional.empty();
        }
        val key = getJsonSigningWebKeyFromJwks(keySet);
        if (key == null) {
            LOGGER.warn("Unable to locate public key from [{}]", keySet);
            return Optional.empty();
        }
        LOGGER.debug("Found public JSON web key as [{}]", key);
        return Optional.of(key);
    }

    /**
     * Gets json signing web key from jwks.
     *
     * @param jwks the jwks
     * @return the json signing web key from jwks
     */
    protected PublicJsonWebKey getJsonSigningWebKeyFromJwks(final JsonWebKeySet jwks) {
        if (jwks.getJsonWebKeys().isEmpty()) {
            LOGGER.warn("No JSON web keys are available in the keystore");
            return null;
        }

        val key = jwks.getJsonWebKeys()
            .stream()
            .filter(k -> k instanceof PublicJsonWebKey)
            .filter(k -> OidcJsonWebKeystoreRotationService.JsonWebKeyLifecycleStates.getJsonWebKeyState(k).isCurrent())
            .findFirst()
            .map(PublicJsonWebKey.class::cast)
            .orElseThrow(() -> new RuntimeException("Unable to locate current JSON web key from the keystore"));

        if (StringUtils.isBlank(key.getAlgorithm())) {
            LOGGER.debug("Located JSON web key [{}] has no algorithm defined", key);
        }
        if (StringUtils.isBlank(key.getKeyId())) {
            LOGGER.debug("Located JSON web key [{}] has no key id defined", key);
        }

        if (key.getPrivateKey() == null) {
            LOGGER.warn("Located JSON web key [{}] has no private key", key);
            return null;
        }
        return key;
    }

    /**
     * Build json web key set.
     *
     * @param resource the resource
     * @return the json web key set
     * @throws Exception the exception
     */
    protected JsonWebKeySet buildJsonWebKeySet(final Resource resource) throws Exception {
        val jsonWebKeySet = OidcJsonWebKeystoreGeneratorService.toJsonWebKeyStore(resource);
        val webKey = getJsonSigningWebKeyFromJwks(jsonWebKeySet);
        if (webKey == null || webKey.getPrivateKey() == null) {
            LOGGER.warn("JSON web key retrieved [{}] is not found or has no associated private key", webKey);
            return null;
        }
        return jsonWebKeySet;
    }

    /**
     * Build json web key set.
     *
     * @return the json web key set
     */
    protected Optional<JsonWebKeySet> buildJsonWebKeySet() {
        try {
            val resource = generateJwksResource();
            if (resource == null) {
                LOGGER.warn("Unable to load or generate a JWKS resource");
                return Optional.empty();
            }
            LOGGER.trace("Retrieving default JSON web key from [{}]", resource);
            val jsonWebKeySet = buildJsonWebKeySet(resource);

            if (jsonWebKeySet == null || jsonWebKeySet.getJsonWebKeys().isEmpty()) {
                LOGGER.warn("No JSON web keys could be found");
                return Optional.empty();
            }
            val badKeysCount = jsonWebKeySet.getJsonWebKeys().stream().filter(k ->
                StringUtils.isBlank(k.getAlgorithm())
                && StringUtils.isBlank(k.getKeyId())
                && StringUtils.isBlank(k.getKeyType())).count();

            if (badKeysCount == jsonWebKeySet.getJsonWebKeys().size()) {
                LOGGER.warn("No valid JSON web keys could be found. The keys that are found in the keystore "
                            + "do not define an algorithm, key id or key type and cannot be used for JWKS operations.");
                return Optional.empty();
            }

            val webKey = getJsonSigningWebKeyFromJwks(jsonWebKeySet);
            if (webKey != null && webKey.getPrivateKey() == null) {
                LOGGER.warn("JSON web key retrieved [{}] has no associated private key.", webKey.getKeyId());
                return Optional.empty();
            }
            LOGGER.trace("Loaded JSON web key set as [{}]", jsonWebKeySet.toJson());
            return Optional.of(jsonWebKeySet);
        } catch (final Exception e) {
            LoggingUtils.warn(LOGGER, e);
        }
        return Optional.empty();
    }

    /**
     * Generate jwks resource.
     *
     * @return the resource
     * @throws Exception the exception
     */
    protected Resource generateJwksResource() throws Exception {
        val resource = getOidcJsonWebKeystoreGeneratorService().generate();
        LOGGER.debug("Loading default JSON web key from [{}]", resource);
        return resource;
    }
}
