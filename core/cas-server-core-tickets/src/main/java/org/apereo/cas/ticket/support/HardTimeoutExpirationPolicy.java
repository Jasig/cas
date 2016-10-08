package org.apereo.cas.ticket.support;

import org.apereo.cas.ticket.TicketState;

import javax.annotation.PostConstruct;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Ticket expiration policy based on a hard timeout from ticket creation time rather than the
 * "idle" timeout provided by {@link TimeoutExpirationPolicy}.
 *
 * @author Andrew Feller
 * @since 3.1.2
 */
public class HardTimeoutExpirationPolicy extends AbstractCasExpirationPolicy {

    /** Serialization support. */
    private static final long serialVersionUID = 6728077010285422290L;

    /** The time to kill in milliseconds. */
    private long timeToKillInMilliSeconds;

    /** No-arg constructor for serialization support. */
    public HardTimeoutExpirationPolicy() {}


    /**
     * Instantiates a new hard timeout expiration policy.
     *
     * @param timeToKillInMilliSeconds the time to kill in milli seconds
     */
    public HardTimeoutExpirationPolicy(final long timeToKillInMilliSeconds) {
        this.timeToKillInMilliSeconds = timeToKillInMilliSeconds;
    }

    /**
     * Instantiates a new Hard timeout expiration policy.
     *
     * @param timeToKill the time to kill
     * @param timeUnit the time unit
     */
    public HardTimeoutExpirationPolicy(final long timeToKill, final TimeUnit timeUnit) {
        this.timeToKillInMilliSeconds = timeUnit.toMillis(timeToKill);
    }


    /**
     * Init .
     */
    @PostConstruct
    public void init() {
        this.timeToKillInMilliSeconds = TimeUnit.SECONDS.toMillis(this.timeToKillInMilliSeconds);
    }

    @Override
    public boolean isExpired(final TicketState ticketState) {
        return ticketState == null || ticketState.getCreationTime()
          .plus(this.timeToKillInMilliSeconds, ChronoUnit.MILLIS).isBefore(ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public Long getTimeToLive() {
        return this.timeToKillInMilliSeconds;
    }

    @Override
    public Long getTimeToIdle() {
        return 0L;
    }
}
