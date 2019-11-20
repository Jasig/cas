package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.ehcache.Ehcache3Properties;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketCatalog;
import org.apereo.cas.ticket.TicketDefinition;
import org.apereo.cas.ticket.registry.EhCache3TicketRegistry;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.util.CoreTicketUtils;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.Status;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.CacheManagerConfiguration;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.statistics.DefaultStatisticsService;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.time.Duration;

/**
 * This is {@link Ehcache3TicketRegistryConfiguration}.
 *
 * @author Hal Deadman
 * @since 6.2.0
 */
@Configuration("ehcache3TicketRegistryConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class Ehcache3TicketRegistryConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Bean
    @ConditionalOnMissingBean
    public CacheManagerConfiguration<PersistentCacheManager> ehcache3CacheManagerConfiguration() {
        val ehcacheProperties = casProperties.getTicket().getRegistry().getEhcache3();

        val terracottaClusterUri = ehcacheProperties.getTerracottaClusterUri();
        if (StringUtils.isNotBlank(terracottaClusterUri)) {
            val clusterConfigBuilder = ClusteringServiceConfigurationBuilder.cluster(URI.create(terracottaClusterUri));
            val connectionMode = ehcacheProperties.getConnectionMode();
            if (Ehcache3Properties.CONNECTION_MODE_AUTOCREATE.equals(connectionMode)) {
                clusterConfigBuilder.autoCreate(s ->
                    s.defaultServerResource(ehcacheProperties.getDefaultServerResource())
                     .resourcePool(ehcacheProperties.getResourcePoolName(), ehcacheProperties.getResourcePoolSize(), MemoryUnit.valueOf(ehcacheProperties.getResourcePoolUnits())));
            } else if (Ehcache3Properties.CONNECTION_MODE_CONFIGLESS.equals(connectionMode)) {
                // TODO not sure what this is
                LOGGER.debug("Connecting to terracotta in config-less mode, cluster tier manager must already exist.");
            }
            return clusterConfigBuilder.build();
        }
        return CacheManagerBuilder.persistence(ehcacheProperties.getRootDirectory());
    }


    @Bean
    @ConditionalOnMissingBean
    public CacheManager ehcache3TicketCacheManager(
        @Qualifier ("ehcache3CacheManagerConfiguration") final CacheManagerConfiguration<PersistentCacheManager> cacheManagerConfiguration) {
        val beanBuilder = CacheManagerBuilder.newCacheManagerBuilder();
        val statisticsService = new DefaultStatisticsService();
        beanBuilder.with(cacheManagerConfiguration)
            .using(statisticsService);

        return beanBuilder.build();
    }

    private CacheConfiguration<String, Ticket> buildCache(final TicketDefinition ticketDefinition) {
        val ehcacheProperties = casProperties.getTicket().getRegistry().getEhcache3();

        CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration = CacheEventListenerConfigurationBuilder
            .newEventListenerConfiguration(new CasCacheEventListener(),
                EventType.CREATED, EventType.UPDATED, EventType.EXPIRED, EventType.REMOVED, EventType.EVICTED)
            .unordered().synchronous();

        val storageTimeout = ticketDefinition.getProperties().getStorageTimeout();
        val expiryPolicy = ehcacheProperties.isEternal()
            ?
            ExpiryPolicyBuilder.noExpiration()
            :
            ExpiryPolicyBuilder.expiry()
            .create(Duration.ofSeconds(storageTimeout))
            .access(Duration.ofSeconds(storageTimeout))
            .update(Duration.ofSeconds(storageTimeout)).build();

        val resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder();
        resourcePoolsBuilder.with(ClusteredResourcePoolBuilder.clustered());

        val cacheConfigBuilder = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class,
            Ticket.class,
            ResourcePoolsBuilder.heap(ehcacheProperties.getMaxElementsInMemory())
                .with(ClusteredResourcePoolBuilder.clustered())
                .offheap(ehcacheProperties.getMaxSizeOffHeap(), MemoryUnit.valueOf(ehcacheProperties.getMaxSizeOffHeapUnits()))
                .disk(ehcacheProperties.getMaxSizeOnDisk(), MemoryUnit.valueOf(ehcacheProperties.getMaxSizeOnDiskUnits())))
            .withExpiry(expiryPolicy)
            .withService(cacheEventListenerConfiguration);

        return cacheConfigBuilder.build();
    }

    private static class CasCacheEventListener implements CacheEventListener<String, Ticket> {

        @Override
        public void onEvent(final CacheEvent<? extends String, ? extends Ticket> event) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Event Type: {}, Ticket Id: {}", event.getType().name(), event.getKey());
            }
        }
    }

    /**
     * Create ticket registry bean with all nececessary caches.
     * Using the spring ehcache wrapper bean so it can be initialized after the caches are built.
     * @param ehcacheManager Spring EhCache manager bean, wraps EhCache manager and is used for cache actuator endpoint.
     * @param ticketCatalog Ticket Catalog
     * @return Ticket Registry
     */
    @Autowired
    @Bean
    @RefreshScope
    public TicketRegistry ticketRegistry(@Qualifier("ehcache3TicketCacheManager") final CacheManager ehcacheManager,
                                         @Qualifier("ticketCatalog") final TicketCatalog ticketCatalog) {
        val crypto = casProperties.getTicket().getRegistry().getEhcache().getCrypto();

        if (Status.UNINITIALIZED.equals(ehcacheManager.getStatus())) {
            ehcacheManager.init();
        }

        val definitions = ticketCatalog.findAll();
        definitions.forEach(t -> {
            val ehcacheConfiguration = buildCache(t);
            ehcacheManager.createCache(t.getProperties().getStorageName(), ehcacheConfiguration);
        });

        return new EhCache3TicketRegistry(ticketCatalog, ehcacheManager, CoreTicketUtils.newTicketRegistryCipherExecutor(crypto, "ehcache"));
    }

}
