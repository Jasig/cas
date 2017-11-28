package org.apereo.cas.ticket.registry;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.apereo.cas.serializer.TicketSerializer;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketCatalog;
import org.apereo.cas.ticket.TicketDefinition;
import org.apereo.cas.ticket.TicketState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author David Rodriguez
 *
 * @since 5.2.0
 */
public class CassandraTicketRegistry extends AbstractTicketRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraTicketRegistry.class);
    private static final String LAST_RUN_TABLE = "ticket_cleaner_lastrun";
    private static final String EXPIRY_TABLE = "ticket_cleaner";

    private static final int FIRST_COLUMN_INDEX = 0;
    private static final long TEN_SECONDS = 10000L;
    private static final int TEN = 10;
    private static final int TICKETS_IN_TESTS = 10;

    private final TicketCatalog ticketCatalog;
    private final TicketSerializer<String> serializer;

    private final PreparedStatement insertTgtStmt;
    private final PreparedStatement updateTgtStmt;
    private final PreparedStatement selectTgtStmt;
    private final PreparedStatement deleteTgtStmt;

    private final PreparedStatement insertStStmt;
    private final PreparedStatement updateStStmt;
    private final PreparedStatement selectStStmt;
    private final PreparedStatement deleteStStmt;

    private final PreparedStatement selectExStmt;
    private final PreparedStatement selectDateExStmt;

    private final PreparedStatement selectLrStmt;
    private final PreparedStatement updateLrStmt;

    private final Session session;
    private final String tgtTable;
    private final String stTable;

    public CassandraTicketRegistry(final Session session, final TicketCatalog ticketCatalog,
                                   final TicketSerializer<String> serializer) {
        this.ticketCatalog = ticketCatalog;
        this.serializer = serializer;

        this.session = session;

        tgtTable = ticketCatalog.find("TGT").getProperties().getStorageName();
        stTable = ticketCatalog.find("ST").getProperties().getStorageName();
        this.selectTgtStmt = session.prepare("select ticket from " + tgtTable + " where id = ?");
        this.insertTgtStmt = session.prepare("insert into " + tgtTable + " (id, ticket, ticket_granting_ticket_id, expiration_bucket) values (?, ?, ?, ?) ");
        this.deleteTgtStmt = session.prepare("delete from " + tgtTable + " where id = ?");
        this.updateTgtStmt = session.prepare("update " + tgtTable + " set ticket = ?, expiration_bucket = ? where id = ? ");

        this.selectStStmt = session.prepare("select ticket from " + stTable + " where id = ?").setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
        this.insertStStmt = session.prepare("insert into " + stTable + " (id, ticket) values (?, ?) ").setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
        this.deleteStStmt = session.prepare("delete from " + stTable + " where id = ?").setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
        this.updateStStmt = session.prepare("update " + stTable + " set ticket = ? where id = ? ").setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);

        this.selectExStmt = session.prepare("select ticket, id from " + EXPIRY_TABLE + " where expiration_bucket = ? ");
        this.selectDateExStmt = session.prepare("select expiration_bucket from " + EXPIRY_TABLE);

        this.selectLrStmt = session.prepare("select last_run from " + LAST_RUN_TABLE + " where id = 'LASTRUN' ");
        this.updateLrStmt = session.prepare("update " + LAST_RUN_TABLE + " set last_run = ? where id = 'LASTRUN' ");

        final long lastRun = getLastRunTimestamp();
        final long currentTime = currentTimeBucket();
        if (lastRun == 0 || lastRun > currentTime) {
            updateLastRunTimestamp(currentTime);
        }
    }

    @Override
    public void addTicket(final Ticket ticket) {
        final String ticketId = ticket.getId();
        LOGGER.debug("Inserting ticket {}", ticketId);
        final TicketDefinition ticketDefinition = ticketCatalog.find(ticketId);
        final String storageName = ticketDefinition.getProperties().getStorageName();

        if (tgtTable.equals(storageName)) {
            final String parentTgtId = ticket.getGrantingTicket() == null ? null : ticket.getGrantingTicket().getId();
            session.execute(this.insertTgtStmt.bind(ticket.getId(), serializer.serialize(ticket), parentTgtId, calculateExpirationDate(ticket) / TEN));
        } else if (stTable.equals(storageName)) {
            session.execute(this.insertStStmt.bind(ticket.getId(), serializer.serialize(ticket)));
        } else {
            LOGGER.error("Failed to insert ticket type {}", ticket.getClass().getName());
        }
    }

    @Override
    public long deleteAll() {
        return TICKETS_IN_TESTS;
    }

    @Override
    public boolean deleteSingleTicket(final String ticketId) {
        LOGGER.debug("Deleting ticket {}", ticketId);
        if (ticketId == null) {
            return false;
        }
        final TicketDefinition ticketDefinition = ticketCatalog.find(ticketId);
        final String storageName = ticketDefinition.getProperties().getStorageName();
        if (tgtTable.equals(storageName)) {
            return session.execute(this.deleteTgtStmt.bind(ticketId)).wasApplied();
        } else if (stTable.equals(storageName)) {
            session.executeAsync(this.deleteStStmt.bind(ticketId));
            return true;
        } else {
            LOGGER.error("Failed to delete ticket type {}", ticketId);
            return false;
        }
    }

    @Override
    public Ticket getTicket(final String ticketId) {
        LOGGER.debug("Querying ticket {}", ticketId);
        if (ticketId == null) {
            return null;
        }
        final TicketDefinition ticketDefinition = ticketCatalog.find(ticketId);
        if (ticketDefinition == null) {
            return null;
        }
        final PreparedStatement statement = getTicketQueryForStorageName(ticketDefinition);
        final Row row = session.execute(statement.bind(ticketId)).one();
        if (row == null) {
            LOGGER.info("ticket {} not found", ticketId);
            return null;
        }
        return serializer.deserialize(row.getString(FIRST_COLUMN_INDEX), ticketDefinition.getImplementationClass());
    }

    private PreparedStatement getTicketQueryForStorageName(final TicketDefinition ticketDefinition) {
        final String storageName = ticketDefinition.getProperties().getStorageName();
        if (tgtTable.equals(storageName)) {
            return this.selectTgtStmt;
        } else if (stTable.equals(storageName)) {
            return this.selectStStmt;
        }
        LOGGER.error("Requesting unknown ticket type {}", ticketDefinition.getImplementationClass());
        return null;
    }

    @Override
    public Ticket updateTicket(final Ticket ticket) {
        final String ticketId = ticket.getId();
        LOGGER.debug("Updating ticket {}", ticketId);
        final TicketDefinition ticketDefinition = ticketCatalog.find(ticketId);
        final String storageName = ticketDefinition.getProperties().getStorageName();
        if (tgtTable.equals(storageName)) {
            session.execute(this.updateTgtStmt.bind(serializer.serialize(ticket), calculateExpirationDate(ticket) / TEN, ticket.getId()));
        } else if (stTable.equals(storageName)) {
            session.execute(this.updateStStmt.bind(serializer.serialize(ticket), ticket.getId()));
        } else {
            LOGGER.error("Failed to update ticket type {}", ticket.getClass().getName());
        }
        return ticket;
    }

    @Override
    public Collection<Ticket> getTickets() {
        final long lastRun = getLastRunTimestamp();
        final long currentTime = currentTimeBucket();

        LOGGER.debug("Searching for expired tickets. LastRun: {}; CurrentTime: {}", lastRun, currentTime);

        return LongStream.rangeClosed(lastRun, currentTime)
                .mapToObj(time -> {
                    updateLastRunTimestamp(time);
                    return getExpiredTGTsIn(time);
                })
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    private Stream<Ticket> getExpiredTGTsIn(final long lastRunBucket) {
        final ResultSet resultSet = session.execute(this.selectExStmt.bind(lastRunBucket));
        return StreamSupport.stream(resultSet.spliterator(), false)
                .map(row -> serializer.deserialize(row.getString(FIRST_COLUMN_INDEX),
                        ticketCatalog.find(row.getString(1)).getImplementationClass()))
                .filter(ticket -> Objects.nonNull(ticket) && ticket.isExpired());
    }

    private long getLastRunTimestamp() {
        final Row row = session.execute(this.selectLrStmt.bind()).one();
        if (row == null) {
            final List<Row> all = session.execute(this.selectDateExStmt.bind()).all();
            return all.stream()
                    .mapToLong(r -> r.getLong(FIRST_COLUMN_INDEX))
                    .min()
                    .orElseGet(CassandraTicketRegistry::currentTimeBucket);
        } else {
            return row.getLong(FIRST_COLUMN_INDEX);
        }
    }

    private void updateLastRunTimestamp(final long timestamp) {
        session.execute(this.updateLrStmt.bind(timestamp));
    }

    private static long currentTimeBucket() {
        return System.currentTimeMillis() / TEN_SECONDS;
    }

    private static long calculateExpirationDate(final Ticket ticket) {
        final ZonedDateTime ticketTtl = ticket.getCreationTime().plusSeconds(ticket.getExpirationPolicy().getTimeToLive());
        final ZonedDateTime ticketTtk = ((TicketState) ticket).getLastTimeUsed().plusSeconds(ticket.getExpirationPolicy().getTimeToIdle());

        return ticketTtl.isBefore(ticketTtk) ? ticketTtl.toEpochSecond() : ticketTtk.toEpochSecond();
    }
}
