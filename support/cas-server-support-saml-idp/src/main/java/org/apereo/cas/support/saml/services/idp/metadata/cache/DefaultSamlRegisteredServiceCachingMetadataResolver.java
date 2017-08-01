package org.apereo.cas.support.saml.services.idp.metadata.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.opensaml.saml.metadata.resolver.ChainingMetadataResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * An adaptation of metadata resolver which handles the resolution of metadata resources
 * inside a cache. It basically is a fancy wrapper around a cache, and constructs the cache
 * semantics before processing the resolution of metadata for a SAML service.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class DefaultSamlRegisteredServiceCachingMetadataResolver implements SamlRegisteredServiceCachingMetadataResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSamlRegisteredServiceCachingMetadataResolver.class);
    private static final int MAX_CACHE_SIZE = 10_000;
    
    private final long metadataCacheExpirationMinutes;
    private final ChainingMetadataResolverCacheLoader chainingMetadataResolverCacheLoader;
    private final LoadingCache<SamlRegisteredService, ChainingMetadataResolver> cache;

    public DefaultSamlRegisteredServiceCachingMetadataResolver(final long metadataCacheExpirationMinutes,
                                                               final ChainingMetadataResolverCacheLoader chainingMetadataResolverCacheLoader) {
        this.metadataCacheExpirationMinutes = metadataCacheExpirationMinutes;
        this.chainingMetadataResolverCacheLoader = chainingMetadataResolverCacheLoader;
        this.cache = Caffeine.newBuilder()
                .maximumSize(MAX_CACHE_SIZE)
                .expireAfterWrite(this.metadataCacheExpirationMinutes, TimeUnit.MINUTES)
                .build(this.chainingMetadataResolverCacheLoader);
    }

    @Override
    public ChainingMetadataResolver resolve(final SamlRegisteredService service) {
        ChainingMetadataResolver resolver = null;
        try {
            LOGGER.debug("Resolving metadata for [{}] at [{}].", service.getName(), service.getMetadataLocation());
            resolver = this.cache.get(service);
            return resolver;
        } catch (final Exception e) {
            throw new IllegalArgumentException("Metadata resolver could not be located from metadata "
                    + service.getMetadataLocation(), e);
        } finally {
            if (resolver != null) {
                LOGGER.debug("Loaded and cached SAML metadata [{}] from [{}] for [{}] minute(s)",
                        resolver.getId(),
                        service.getMetadataLocation(),
                        this.metadataCacheExpirationMinutes);
            }
        }
    }
}
