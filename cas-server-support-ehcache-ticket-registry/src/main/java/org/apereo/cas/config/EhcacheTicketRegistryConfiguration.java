package org.apereo.cas.config;

import com.google.common.collect.ImmutableSet;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.distribution.RMIBootstrapCacheLoader;
import net.sf.ehcache.distribution.RMISynchronousCacheReplicator;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.ticket.registry.EhCacheTicketRegistry;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.util.ResourceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;

/**
 * This is {@link EhcacheTicketRegistryConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("ehcacheTicketRegistryConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class EhcacheTicketRegistryConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Nullable
    @Autowired(required = false)
    @Qualifier("ticketCipherExecutor")
    private CipherExecutor cipherExecutor;

    @RefreshScope
    @Bean
    public RMISynchronousCacheReplicator ticketRMISynchronousCacheReplicator() {

        return new RMISynchronousCacheReplicator(
                casProperties.getTicket().getRegistry().getEhcache().isReplicatePuts(),
                casProperties.getTicket().getRegistry().getEhcache().isReplicatePutsViaCopy(),
                casProperties.getTicket().getRegistry().getEhcache().isReplicateUpdates(),
                casProperties.getTicket().getRegistry().getEhcache().isReplicateUpdatesViaCopy(),
                casProperties.getTicket().getRegistry().getEhcache().isReplicateRemovals());
    }

    /**
     * Ticket cache bootstrap cache loader rmi bootstrap cache loader.
     *
     * @return the rmi bootstrap cache loader
     */
    @RefreshScope
    @Bean
    public RMIBootstrapCacheLoader ticketCacheBootstrapCacheLoader() {
        return new RMIBootstrapCacheLoader(casProperties.getTicket().getRegistry().getEhcache().isLoaderAsync(),
                casProperties.getTicket().getRegistry().getEhcache().getMaxChunkSize());
    }


    /**
     * Cache manager eh cache manager factory bean.
     *
     * @return the eh cache manager factory bean
     */
    @RefreshScope
    @Bean
    public EhCacheManagerFactoryBean cacheManager() {
        final EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
        bean.setConfigLocation(ResourceUtils.prepareClasspathResourceIfNeeded(
                casProperties.getTicket().getRegistry().getEhcache().getConfigLocation()));
        bean.setShared(casProperties.getTicket().getRegistry().getEhcache().isShared());
        bean.setCacheManagerName(casProperties.getTicket().getRegistry().getEhcache().getCacheManagerName());

        return bean;
    }

    /**
     * Service tickets cache eh cache factory bean.
     *
     * @param manager the manager
     * @return the eh cache factory bean
     */
    @RefreshScope
    @Bean
    public EhCacheFactoryBean ehcacheTicketsCache(@Qualifier("cacheManager")
                                                  final CacheManager manager) {
        final EhCacheFactoryBean bean = new EhCacheFactoryBean();
        bean.setCacheName(casProperties.getTicket().getRegistry().getEhcache().getCacheName());
        bean.setCacheEventListeners(ImmutableSet.of(ticketRMISynchronousCacheReplicator()));
        bean.setTimeToIdle(casProperties.getTicket().getRegistry().getEhcache().getCacheTimeToIdle());
        bean.setTimeToLive(casProperties.getTicket().getRegistry().getEhcache().getCacheTimeToLive());

        bean.setCacheManager(manager);
        bean.setBootstrapCacheLoader(ticketCacheBootstrapCacheLoader());

        bean.setDiskExpiryThreadIntervalSeconds(
                casProperties.getTicket().getRegistry().getEhcache().getDiskExpiryThreadIntervalSeconds());
        bean.setDiskPersistent(casProperties.getTicket().getRegistry().getEhcache().isDiskPersistent());
        bean.setEternal(casProperties.getTicket().getRegistry().getEhcache().isEternal());
        bean.setMaxElementsInMemory(casProperties.getTicket().getRegistry().getEhcache().getMaxElementsInMemory());
        bean.setMaxElementsOnDisk(casProperties.getTicket().getRegistry().getEhcache().getMaxElementsOnDisk());
        bean.setMemoryStoreEvictionPolicy(casProperties.getTicket().getRegistry().getEhcache().getMemoryStoreEvictionPolicy());
        bean.setOverflowToDisk(casProperties.getTicket().getRegistry().getEhcache().isOverflowToDisk());

        return bean;
    }

    @RefreshScope
    @Bean(name = {"ehcacheTicketRegistry", "ticketRegistry"})
    public TicketRegistry ehcacheTicketRegistry(@Qualifier("ehcacheTicketsCache")
                                                final Cache ehcacheTicketsCache) {
        final EhCacheTicketRegistry r = new EhCacheTicketRegistry(ehcacheTicketsCache);
        r.setCipherExecutor(cipherExecutor);
        return r;
    }
}
