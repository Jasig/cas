package org.apereo.cas.config;

import org.apereo.cas.audit.MongoDbAuditTrailManager;
import org.apereo.cas.audit.spi.DefaultDelegatingAuditTrailManager;
import org.apereo.cas.audit.spi.DelegatingAuditTrailManager;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.core.audit.AuditProperties;
import org.apereo.cas.mongo.MongoDbObjectFactory;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * This is {@link CasSupportMongoDbAuditConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Configuration("casSupportMongoDbAuditConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasSupportMongoDbAuditConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Bean
    public AuditTrailManager mongoDbAuditTrailManager() {
        final AuditProperties.MongoDb mongo = casProperties.getAudit().getMongo();
        final MongoDbObjectFactory factory = new MongoDbObjectFactory();
        final MongoTemplate mongoTemplate = factory.buildMongoTemplate(mongo);
        factory.createCollection(mongoTemplate, mongo.getCollection(), mongo.isDropCollection());
        return new MongoDbAuditTrailManager(mongoTemplate, mongo.getCollection());
    }

    @Bean
    public DelegatingAuditTrailManager auditTrailManager() {
        return new DefaultDelegatingAuditTrailManager(mongoDbAuditTrailManager());
    }

}
