package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.ticket.TicketCatalog;
import org.apereo.cas.ticket.TicketDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link CassandraTicketRegistryTicketCatalogConfiguration}.
 *
 * @author David Rodriguez
 * @since 5.1.0
 */
@Configuration("hazelcastTicketRegistryTicketMetadataCatalogConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CassandraTicketRegistryTicketCatalogConfiguration extends CasCoreTicketCatalogConfiguration {

    @Override
    protected void buildAndRegisterServiceTicketDefinition(final TicketCatalog plan, final TicketDefinition metadata) {
        setServiceTicketDefinitionProperties(metadata);
        super.buildAndRegisterServiceTicketDefinition(plan, metadata);
    }

    @Override
    protected void buildAndRegisterProxyTicketDefinition(final TicketCatalog plan, final TicketDefinition metadata) {
        setServiceTicketDefinitionProperties(metadata);
        super.buildAndRegisterServiceTicketDefinition(plan, metadata);
    }

    @Override
    protected void buildAndRegisterTicketGrantingTicketDefinition(final TicketCatalog plan, final TicketDefinition metadata) {
        setTicketGrantingTicketProperties(metadata);
        super.buildAndRegisterTicketGrantingTicketDefinition(plan, metadata);
    }

    @Override
    protected void buildAndRegisterProxyGrantingTicketDefinition(final TicketCatalog plan, final TicketDefinition metadata) {
        setTicketGrantingTicketProperties(metadata);
        super.buildAndRegisterTicketGrantingTicketDefinition(plan, metadata);
    }

    private void setTicketGrantingTicketProperties(final TicketDefinition metadata) {
        metadata.getProperties().setStorageName("ticketGrantingTicket");
    }

    private void setServiceTicketDefinitionProperties(final TicketDefinition metadata) {
        metadata.getProperties().setStorageName("serviceTicket");
    }
}
