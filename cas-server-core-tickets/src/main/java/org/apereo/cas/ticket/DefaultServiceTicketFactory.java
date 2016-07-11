package org.apereo.cas.ticket;

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.util.DefaultUniqueTicketIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The {@link DefaultServiceTicketFactory} is responsible for
 * creating {@link ServiceTicket} objects.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
public class DefaultServiceTicketFactory implements ServiceTicketFactory {
    protected transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Default instance for the ticket id generator. */
    protected UniqueTicketIdGenerator defaultServiceTicketIdGenerator = new DefaultUniqueTicketIdGenerator();
    
    /** Map to contain the mappings of service to {@link UniqueTicketIdGenerator}s. */
    protected Map<String, UniqueTicketIdGenerator> uniqueTicketIdGeneratorsForService;
    
    /** ExpirationPolicy for Service Tickets. */
    protected ExpirationPolicy serviceTicketExpirationPolicy;
    
    private boolean trackMostRecentSession = true;

    @Override
    public <T extends Ticket> T create(final TicketGrantingTicket ticketGrantingTicket,
                                       final Service service,
                                       final Authentication currentAuthentication) {

        final String uniqueTicketIdGenKey = service.getClass().getName();
        UniqueTicketIdGenerator serviceTicketUniqueTicketIdGenerator = null;
        if (this.uniqueTicketIdGeneratorsForService != null && !this.uniqueTicketIdGeneratorsForService.isEmpty()) {
            logger.debug("Looking up service ticket id generator for [{}]", uniqueTicketIdGenKey);
            serviceTicketUniqueTicketIdGenerator = this.uniqueTicketIdGeneratorsForService.get(uniqueTicketIdGenKey);
        }
        if (serviceTicketUniqueTicketIdGenerator == null) {
            serviceTicketUniqueTicketIdGenerator = this.defaultServiceTicketIdGenerator;
            logger.debug("Service ticket id generator not found for [{}]. Using the default generator...",
                    uniqueTicketIdGenKey);
        }

        final String ticketId = serviceTicketUniqueTicketIdGenerator.getNewTicketId(ServiceTicket.PREFIX);
        final ServiceTicket serviceTicket = ticketGrantingTicket.grantServiceTicket(
                ticketId,
                service,
                this.serviceTicketExpirationPolicy,
                currentAuthentication,
                trackMostRecentSession);
        return (T) serviceTicket;
    }

    @Override
    public <T extends TicketFactory> T get(final Class<? extends Ticket> clazz) {
        return (T) this;
    }
    
    public void setUniqueTicketIdGeneratorsForService(final Map<String, UniqueTicketIdGenerator> uniqueTicketIdGeneratorsForService) {
        this.uniqueTicketIdGeneratorsForService = uniqueTicketIdGeneratorsForService;
    }

    public void setServiceTicketExpirationPolicy(final ExpirationPolicy serviceTicketExpirationPolicy) {
        this.serviceTicketExpirationPolicy = serviceTicketExpirationPolicy;
    }

    public void setDefaultServiceTicketIdGenerator(final UniqueTicketIdGenerator defaultServiceTicketIdGenerator) {
        this.defaultServiceTicketIdGenerator = defaultServiceTicketIdGenerator;
    }

    public void setTrackMostRecentSession(final boolean trackMostRecentSession) {
        this.trackMostRecentSession = trackMostRecentSession;
    }
}
