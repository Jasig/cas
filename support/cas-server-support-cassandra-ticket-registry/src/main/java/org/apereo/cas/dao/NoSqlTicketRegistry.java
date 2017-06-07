package org.apereo.cas.dao;

import org.apereo.cas.ticket.ServiceTicket;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.AbstractTicketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author David Rodriguez
 *
 * @since 5.1.0
 */
public class NoSqlTicketRegistry extends AbstractTicketRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoSqlTicketRegistry.class);

    private NoSqlTicketRegistryDao ticketRegistryDao;

    public NoSqlTicketRegistry(final NoSqlTicketRegistryDao ticketRegistryDao) {
        this.ticketRegistryDao = ticketRegistryDao;
    }

    @Override
    public void addTicket(final Ticket ticket) {
        final String ticketId = ticket.getId();
        LOGGER.debug("Inserting ticket {}", ticketId);
        if (isTgt(ticketId)) {
            ticketRegistryDao.addTicketGrantingTicket(ticket);
        } else if (isSt(ticketId)) {
            ticketRegistryDao.addServiceTicket(ticket);
        } else {
            LOGGER.error("Inserting unknown ticket type {}", ticket.getClass().getName());
        }
    }

    @Override
    public int deleteTicket(final String id) {
        if (deleteSingleTicket(id)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean deleteSingleTicket(final String id) {
        LOGGER.debug("Deleting ticket {}", id);
        if (isTgt(id)) {
            return ticketRegistryDao.deleteTicketGrantingTicket(id);
        } else if (isSt(id)) {
            return ticketRegistryDao.deleteServiceTicket(id);
        } else {
            LOGGER.error("Deleting unknown ticket type {}", id);
            return false;
        }
    }

    @Override
    public Ticket getTicket(final String id) {
        LOGGER.debug("Querying ticket {}", id);
        if (isTgt(id)) {
            return ticketRegistryDao.getTicketGrantingTicket(id);
        } else if (isSt(id)) {
            return ticketRegistryDao.getServiceTicket(id);
        } else {
            LOGGER.error("Requesting unknown ticket type {}", id);
            return null;
        }
    }

    @Override
    public Ticket updateTicket(final Ticket ticket) {
        final String ticketId = ticket.getId();
        LOGGER.debug("Updating ticket {}", ticketId);
        if (isTgt(ticketId)) {
            ticketRegistryDao.updateTicketGrantingTicket(ticket);
        } else if (isSt(ticketId)) {
            ticketRegistryDao.updateServiceTicket(ticket);
        } else {
            LOGGER.error("Updating unknown ticket type {}", ticket.getClass().getName());
        }
        return ticket;
    }

    @Override
    public Collection<Ticket> getTickets() {
        return null;
    }

    private static boolean isSt(final String id) {
        return id.startsWith(ServiceTicket.PREFIX);
    }

    private static boolean isTgt(final String id) {
        return id.startsWith(TicketGrantingTicket.PREFIX);
    }
}
