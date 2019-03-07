package org.apereo.cas.authentication.principal.cache;

import org.apereo.cas.authentication.AttributeMergingStrategy;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalAttributesRepository;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.spring.ApplicationContextProvider;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Synchronized;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apereo.services.persondir.IPersonAttributeDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Parent class for retrieval principals attributes, provides operations
 * around caching, merging of attributes.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@Slf4j
@ToString
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"mergingStrategy", "attributeRepositoryIds"})
public abstract class AbstractPrincipalAttributesRepository implements PrincipalAttributesRepository, AutoCloseable {
    private static final long serialVersionUID = 6350245643948535906L;

    /**
     * The merging strategy that deals with existing principal attributes
     * and those that are retrieved from the source. By default, existing attributes
     * are ignored and the source is always consulted.
     */
    protected AttributeMergingStrategy mergingStrategy = AttributeMergingStrategy.MULTIVALUED;

    private Set<String> attributeRepositoryIds = new LinkedHashSet<>();

    private boolean ignoreResolvedAttributes;

    @Override
    public abstract Map<String, Object> getAttributes(Principal principal, RegisteredService registeredService);

    /**
     * Convert attributes to principal attributes and cache.
     *
     * @param p                the p
     * @param sourceAttributes the source attributes
     * @return the map
     */
    protected Map<String, Object> convertAttributesToPrincipalAttributesAndCache(final Principal p, final Map<String, List<Object>> sourceAttributes) {
        val finalAttributes = convertPersonAttributesToPrincipalAttributes(sourceAttributes);
        addPrincipalAttributes(p.getId(), finalAttributes);
        return finalAttributes;
    }

    /**
     * Add principal attributes into the underlying cache instance.
     *
     * @param id         identifier used by the cache as key.
     * @param attributes attributes to cache
     * @since 4.2
     */
    protected abstract void addPrincipalAttributes(String id, Map<String, Object> attributes);

    /**
     * Gets attribute repository.
     *
     * @return the attribute repository
     */
    @JsonIgnore
    protected static IPersonAttributeDao getAttributeRepository() {
        val repositories = ApplicationContextProvider.getAttributeRepository();
        return repositories.orElse(null);
    }

    /**
     * Calculate merging strategy attribute merging strategy.
     *
     * @return the attribute merging strategy
     */
    protected AttributeMergingStrategy determineMergingStrategy() {
        return ObjectUtils.defaultIfNull(getMergingStrategy(), AttributeMergingStrategy.MULTIVALUED);
    }

    /**
     * Configure attribute repository filter by ids.
     *
     * @param repository             the repository
     * @param attributeRepositoryIds the attribute repository ids
     */
    protected static void configureAttributeRepositoryFilterByIds(final IPersonAttributeDao repository,
                                                                  final Set<String> attributeRepositoryIds) {
        val repoIdsArray = attributeRepositoryIds.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        repository.setPersonAttributeDaoFilter(dao -> Arrays.stream(dao.getId())
            .anyMatch(daoId -> StringUtils.equalsAnyIgnoreCase(daoId, repoIdsArray)));
    }

    /**
     * Are attribute repository ids defined boolean.
     *
     * @return the boolean
     */
    @JsonIgnore
    protected boolean areAttributeRepositoryIdsDefined() {
        return attributeRepositoryIds != null && !attributeRepositoryIds.isEmpty();
    }

    /***
     * Convert principal attributes to person attributes.
     * @param attributes the attributes
     * @return person attributes
     */
    protected static Map<String, List<Object>> convertPrincipalAttributesToPersonAttributes(final Map<String, ?> attributes) {
        val convertedAttributes = new TreeMap<String, List<Object>>(String.CASE_INSENSITIVE_ORDER);
        val principalAttributes = new LinkedHashMap<>(attributes);
        principalAttributes.forEach((key, values) -> {
            if (values instanceof Collection) {
                val uniqueValues = new LinkedHashSet<Object>(Collection.class.cast(values));
                val listedValues = new ArrayList<Object>(uniqueValues);
                convertedAttributes.put(key, listedValues);
            } else {
                convertedAttributes.put(key, CollectionUtils.wrap(values));
            }
        });
        return convertedAttributes;
    }

    /**
     * Convert person attributes to principal attributes.
     *
     * @param attributes person attributes
     * @return principal attributes
     */
    protected static Map<String, Object> convertPersonAttributesToPrincipalAttributes(final Map<String, List<Object>> attributes) {
        return attributes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().size() == 1
            ? entry.getValue().get(0) : entry.getValue(), (e, f) -> f == null ? e : f));
    }

    /**
     * Obtains attributes first from the repository by calling
     * {@link org.apereo.services.persondir.IPersonAttributeDao#getPerson(String)}.
     *
     * @param id the person id to locate in the attribute repository
     * @return the map of attributes
     */
    @Synchronized
    protected Map<String, List<Object>> retrievePersonAttributesFromAttributeRepository(final String id) {
        val repository = getAttributeRepository();
        if (repository == null) {
            LOGGER.warn("No attribute repositories could be fetched from application context");
            return new HashMap<>(0);
        }

        val originalFilter = repository.getPersonAttributeDaoFilter();
        try {
            if (areAttributeRepositoryIdsDefined()) {
                configureAttributeRepositoryFilterByIds(repository, this.attributeRepositoryIds);

                val attrs = repository.getPerson(id);
                if (attrs == null) {
                    LOGGER.debug("Could not find principal [{}] in the repository so no attributes are returned.", id);
                    return new HashMap<>(0);
                }
                val attributes = attrs.getAttributes();
                if (attributes == null) {
                    LOGGER.debug("Principal [{}] has no attributes and so none are returned.", id);
                    return new HashMap<>(0);
                }
                return attributes;
            }
        } finally {
            repository.setPersonAttributeDaoFilter(originalFilter);
        }
        return new HashMap<>(0);
    }

    /**
     * Gets principal attributes.
     *
     * @param principal the principal
     * @return the principal attributes
     */
    @JsonIgnore
    protected Map<String, List<Object>> getPrincipalAttributes(final Principal principal) {
        if (ignoreResolvedAttributes) {
            return new HashMap<>(0);
        }
        return convertPrincipalAttributesToPersonAttributes(principal.getAttributes());
    }

    @Override
    public void close() {
    }
}
