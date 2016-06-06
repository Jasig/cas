package org.apereo.cas.config;

import org.apereo.cas.couchbase.core.CouchbaseClientFactory;
import org.apereo.cas.ticket.registry.CouchbaseTicketRegistry;
import org.apereo.cas.ticket.registry.DefaultTicketRegistryCleaner;
import org.apereo.cas.ticket.registry.TicketRegistryCleaner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * This is {@link CouchbaseTicketRegistryConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("couchbaseTicketRegistryConfiguration")
public class CouchbaseTicketRegistryConfiguration {

    /**
     * The Node set.
     */
    @Value("${ticketreg.couchbase.nodes:localhost:8091}")
    private String nodeSet;

    /**
     * The Timeout.
     */
    @Value("${ticketreg.couchbase.timeout:10}")
    private int timeout;

    /**
     * The Password.
     */
    @Value("${ticketreg.couchbase.password:}")
    private String password;

    /**
     * The Bucket.
     */
    @Value("${ticketreg.couchbase.bucket:default}")
    private String bucket;


    /**
     * Ticket registry couchbase client factory couchbase client factory.
     *
     * @return the couchbase client factory
     */
    @RefreshScope
    @Bean
    public CouchbaseClientFactory ticketRegistryCouchbaseClientFactory() {
        final CouchbaseClientFactory factory = new CouchbaseClientFactory();
        factory.setNodes(StringUtils.commaDelimitedListToSet(this.nodeSet));
        factory.setTimeout(this.timeout);
        factory.setBucketName(this.bucket);
        factory.setPassword(this.password);
        return factory;
    }

    @RefreshScope
    @Bean
    public CouchbaseTicketRegistry couchbaseTicketRegistry() {
        return new CouchbaseTicketRegistry();
    }

    @Bean
    public TicketRegistryCleaner ticketRegistryCleaner() {
        return new DefaultTicketRegistryCleaner() {
            @Override
            protected boolean isCleanerSupported() {
                return false;
            }
        };
    }
}
