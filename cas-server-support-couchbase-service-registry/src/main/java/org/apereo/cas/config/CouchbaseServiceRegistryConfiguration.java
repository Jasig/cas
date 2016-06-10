package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.couchbase.core.CouchbaseClientFactory;
import org.apereo.cas.services.CouchbaseServiceRegistryDao;
import org.apereo.cas.services.ServiceRegistryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * This is {@link CouchbaseServiceRegistryConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("couchbaseServiceRegistryConfiguration")
public class CouchbaseServiceRegistryConfiguration {
    
    @Autowired
    private CasConfigurationProperties casProperties;

    /**
     * Service registry couchbase client factory couchbase client factory.
     *
     * @return the couchbase client factory
     */
    @RefreshScope
    @Bean
    public CouchbaseClientFactory serviceRegistryCouchbaseClientFactory() {
        final CouchbaseClientFactory factory = new CouchbaseClientFactory();
        factory.setNodes(StringUtils.commaDelimitedListToSet(casProperties.getCouchbaseServiceRegistry().getNodeSet()));
        factory.setTimeout(casProperties.getCouchbaseServiceRegistry().getTimeout());
        factory.setBucketName(casProperties.getCouchbaseServiceRegistry().getBucket());
        factory.setPassword(casProperties.getCouchbaseServiceRegistry().getPassword());
        return factory;
    }
    
    @Bean
    @RefreshScope
    public ServiceRegistryDao couchbaseServiceRegistryDao() {
        return new CouchbaseServiceRegistryDao();
    }
}
