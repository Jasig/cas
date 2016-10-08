package org.apereo.cas.ticket.registry;

import com.couchbase.client.java.document.SerializableDocument;
import com.couchbase.client.java.view.DefaultView;
import com.couchbase.client.java.view.View;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.couchbase.core.CouchbaseClientFactory;
import org.apereo.cas.ticket.ServiceTicket;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * A Ticket Registry storage backend which uses the memcached protocol.
 * CouchBase is a multi host NoSQL database with a memcached interface
 * to persistent storage which also is quite usable as a replicated
 * ticket storage engine for multiple front end CAS servers.
 *
 * @author Fredrik Jönsson "fjo@kth.se"
 * @author Misagh Moayyed
 * @since 4.2.0
 */
public class CouchbaseTicketRegistry extends AbstractTicketRegistry {
    private static final String END_TOKEN = "\u02ad";

    private static final String VIEW_NAME_ALL_TICKETS = "all_tickets";

    private static final View ALL_TICKETS_VIEW = DefaultView.create(
            VIEW_NAME_ALL_TICKETS,
            "function(d,m) {emit(m.id);}",
            "_count");
    private static final List<View> ALL_VIEWS = Lists.newArrayList(new View[]{
            ALL_TICKETS_VIEW
    });
    private static final String UTIL_DOCUMENT = "statistics";

    @Autowired
    private CasConfigurationProperties casProperties;

    private CouchbaseClientFactory couchbase;

    /**
     * Default constructor.
     */
    public CouchbaseTicketRegistry() {
    }

    @Override
    public void updateTicket(final Ticket ticket) {
        logger.debug("Updating ticket {}", ticket);
        try {
            final SerializableDocument document =
                    SerializableDocument.create(ticket.getId(),
                            ticket.getExpirationPolicy().getTimeToLive().intValue(), ticket);
            this.couchbase.bucket().upsert(document);
        } catch (final Exception e) {
            logger.error("Failed updating {}: {}", ticket, e);
        }
    }

    @Override
    public void addTicket(final Ticket ticketToAdd) {
        logger.debug("Adding ticket {}", ticketToAdd);
        try {
            final Ticket ticket = encodeTicket(ticketToAdd);
            final SerializableDocument document =
                    SerializableDocument.create(ticket.getId(),
                            ticket.getExpirationPolicy().getTimeToLive().intValue(), ticket);
            this.couchbase.bucket().upsert(document);
        } catch (final Exception e) {
            logger.error("Failed adding {}: {}", ticketToAdd, e);
        }
    }

    @Override
    public Ticket getTicket(final String ticketId) {
        try {
            final String encTicketId = encodeTicketId(ticketId);
            if (ticketId == null) {
                return null;
            }

            final SerializableDocument document = this.couchbase.bucket().get(encTicketId, SerializableDocument.class);
            if (document != null) {
                final Ticket t = (Ticket) document.content();
                logger.debug("Got ticket {} from registry.", t);
                return t;
            }
            logger.debug("Ticket {} not found in registry.", encTicketId);
            return null;
        } catch (final Exception e) {
            logger.error("Failed fetching {}: {}", ticketId, e);
            return null;
        }
    }


    /**
     * Starts the couchbase client.
     */
    @PostConstruct
    public void initialize() {

        System.setProperty("com.couchbase.queryEnabled",
                Boolean.toString(casProperties.getTicket().getRegistry().getCouchbase().isQueryEnabled()));
        this.couchbase.ensureIndexes(UTIL_DOCUMENT, ALL_VIEWS);
        this.couchbase.initialize();
    }


    /**
     * Stops the couchbase client.
     */
    @PreDestroy
    public void destroy() {
        try {
            this.couchbase.shutdown();
        } catch (final Exception e) {
            throw Throwables.propagate(e);
        }
    }
    
    @Override
    public Collection<Ticket> getTickets() {
        return new ArrayList<>();
    }

    @Override
    public long sessionCount() {
        return runQuery(TicketGrantingTicket.PREFIX + '-');
    }

    @Override
    public long serviceTicketCount() {
        return runQuery(ServiceTicket.PREFIX + '-');
    }

    @Override
    public boolean deleteSingleTicket(final String ticketId) {
        logger.debug("Deleting ticket {}", ticketId);
        try {
            return this.couchbase.bucket().remove(ticketId) != null;
        } catch (final Exception e) {
            logger.error("Failed deleting {}: {}", ticketId, e);
            return false;
        }
    }

    private int runQuery(final String prefix) {
        final ViewResult allKeys = this.couchbase.bucket().query(
                ViewQuery.from(UTIL_DOCUMENT, VIEW_NAME_ALL_TICKETS)
                        .startKey(prefix)
                        .endKey(prefix + END_TOKEN)
                        .reduce());
        final Iterator<ViewRow> iterator = allKeys.iterator();
        if (iterator.hasNext()) {
            final ViewRow res = iterator.next();
            return (Integer) res.value();
        }

        return 0;
    }


    public void setCouchbaseClientFactory(final CouchbaseClientFactory couchbase) {
        this.couchbase = couchbase;
    }
}

