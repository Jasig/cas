package org.apereo.cas.ticket.registry;

import org.apereo.cas.logout.LogoutManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author David Rodriguez
 *
 * @since 5.1.0
 */
public class CassandraTicketRegistryCleaner implements TicketRegistryCleaner {

    private final CassandraTicketRegistryDao ticketRegistryDao;
    private final LogoutManager logoutManager;

    public CassandraTicketRegistryCleaner(@Qualifier("cassandraTicketRegistry") final CassandraTicketRegistryDao ticketRegistry,
                                          final LogoutManager logoutManager) {
        this.ticketRegistryDao = ticketRegistry;
        this.logoutManager = logoutManager;
    }

    @Scheduled(initialDelayString = "${cas.ticket.registry.cleaner.startDelay:20000}",
            fixedDelayString = "${cas.ticket.registry.cleaner.repeatInterval:60000}")
    @Override
    public void clean() {
        ticketRegistryDao.getExpiredTgts().forEach(ticket -> {
            logoutManager.performLogout(ticket);
            ticketRegistryDao.deleteTicketGrantingTicket(ticket.getId());
        });
    }
}
