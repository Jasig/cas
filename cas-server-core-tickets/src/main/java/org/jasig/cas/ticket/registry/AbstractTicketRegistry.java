package org.jasig.cas.ticket.registry;

import com.google.common.io.ByteSource;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.CipherExecutor;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.logout.LogoutManager;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.proxy.ProxyGrantingTicket;
import org.jasig.cas.ticket.proxy.ProxyTicket;
import org.jasig.cas.util.DateTimeUtils;
import org.jasig.cas.util.DigestUtils;
import org.jasig.cas.util.Pair;
import org.jasig.cas.util.SerializationUtils;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Scott Battaglia
 * @since 3.0.0
 * <p>
 * This is a published and supported CAS Server 3 API.
 * </p>
 */
public abstract class AbstractTicketRegistry implements TicketRegistry, TicketRegistryState, Job {

    /** The Slf4j logger instance. */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${ticket.registry.cleaner.enabled:true}")
    private boolean cleanerEnabled;

    @Value("${ticket.registry.cleaner.repeatinterval:120}")
    private int refreshInterval;

    @Value("${ticket.registry.cleaner.startdelay:20}")
    private int startDelay;

    @Autowired(required = false)
    @Qualifier("scheduler")
    private Scheduler scheduler;

    @Nullable
    @Autowired(required = false)
    @Qualifier("ticketCipherExecutor")
    private CipherExecutor<byte[], byte[]> cipherExecutor;

    @Autowired
    @Qualifier("logoutManager")
    private LogoutManager logoutManager;

    private List<Pair<Class<? extends Ticket>, Constructor<? extends AbstractTicketDelegator>>> ticketDelegators = new ArrayList<>();

    /**
     * Default constructor which registers the appropriate ticket delegators.
     */
    @SuppressWarnings("unchecked")
    public AbstractTicketRegistry() {
        ticketDelegators.add(new Pair(ProxyGrantingTicket.class,
                AbstractTicketDelegator.getDefaultConstructor(ProxyGrantingTicketDelegator.class)));
        ticketDelegators.add(new Pair(TicketGrantingTicket.class,
                AbstractTicketDelegator.getDefaultConstructor(TicketGrantingTicketDelegator.class)));
        ticketDelegators.add(new Pair(ProxyTicket.class,
                AbstractTicketDelegator.getDefaultConstructor(ProxyTicketDelegator.class)));
        ticketDelegators.add(new Pair(ServiceTicket.class,
                AbstractTicketDelegator.getDefaultConstructor(ServiceTicketDelegator.class)));
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if class is null.
     * @throws ClassCastException if class does not match requested ticket
     * class.
     * @return specified ticket from the registry
     */
    @Override
    public final <T extends Ticket> T getTicket(final String ticketId, final Class<T> clazz) {
        Assert.notNull(clazz, "clazz cannot be null");

        final Ticket ticket = this.getTicket(ticketId);

        if (ticket == null) {
            return null;
        }

        if (!clazz.isAssignableFrom(ticket.getClass())) {
            throw new ClassCastException("Ticket [" + ticket.getId()
                + " is of type " + ticket.getClass()
                + " when we were expecting " + clazz);
        }

        return (T) ticket;
    }

    @Override
    public long sessionCount() {
      logger.debug("sessionCount() operation is not implemented by the ticket registry instance {}. Returning unknown as {}",
                this.getClass().getName(), Long.MIN_VALUE);
      return Long.MIN_VALUE;
    }

    @Override
    public long serviceTicketCount() {
      logger.debug("serviceTicketCount() operation is not implemented by the ticket registry instance {}. Returning unknown as {}",
                this.getClass().getName(), Long.MIN_VALUE);
      return Long.MIN_VALUE;
    }

    @Override
    public final boolean deleteTicket(final String ticketId) {
        if (ticketId == null) {
            return false;
        }

        final Ticket ticket = getTicket(ticketId);
        if (ticket == null) {
            return false;
        }

        if (ticket instanceof TicketGrantingTicket) {
            if (ticket instanceof ProxyGrantingTicket) {
                logger.debug("Removing proxy-granting ticket [{}]", ticketId);
            }

            logger.debug("Removing children of ticket [{}] from the registry.", ticket.getId());
            final TicketGrantingTicket tgt = (TicketGrantingTicket) ticket;
            deleteChildren(tgt);

            final Collection<ProxyGrantingTicket> proxyGrantingTickets = tgt.getProxyGrantingTickets();
            proxyGrantingTickets.stream().map(Ticket::getId).forEach(this::deleteTicket);
        }
        logger.debug("Removing ticket [{}] from the registry.", ticket);
        return deleteSingleTicket(ticketId);
    }


    /**
     * Delete TGT's service tickets.
     *
     * @param ticket the ticket
     */
    private void deleteChildren(final TicketGrantingTicket ticket) {
        // delete service tickets
        final Map<String, Service> services = ticket.getServices();
        if (services != null && !services.isEmpty()) {
            services.keySet().stream().forEach(ticketId -> {
                if (deleteSingleTicket(ticketId)) {
                    logger.debug("Removed ticket [{}]", ticketId);
                } else {
                    logger.debug("Unable to remove ticket [{}]", ticketId);
                }
            });
        }
    }

    /**
     * Delete a single ticket instance from the store.
     *
     * @param ticketId the ticket id
     * @return the boolean
     */
    public boolean deleteSingleTicket(final Ticket ticketId) {
        return deleteSingleTicket(ticketId.getId());
    }

    /**
     * Delete a single ticket instance from the store.
     *
     * @param ticketId the ticket id
     * @return the boolean
     */
    public abstract boolean deleteSingleTicket(final String ticketId);

    /**
     * Update the received ticket.
     *
     * @param ticket the ticket
     */
    protected abstract void updateTicket(Ticket ticket);

    /**
     * Whether or not a callback to the TGT is required when checking for expiration.
     *
     * @return true, if successful
     */
    protected abstract boolean needsCallback();

    /**
     * Gets the proxied ticket instance.
     *
     * @param ticket the ticket
     * @return the proxied ticket instance
     */
    protected final Ticket getProxiedTicketInstance(final Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        for (final Pair<Class<? extends Ticket>, Constructor<? extends AbstractTicketDelegator>> ticketDelegator: ticketDelegators) {
            final Class<? extends Ticket> clazz = ticketDelegator.getFirst();
            if (clazz.isAssignableFrom(ticket.getClass())) {
                final Constructor<? extends AbstractTicketDelegator> constructor = ticketDelegator.getSecond();
                try {
                    return constructor.newInstance(this, ticket, needsCallback());
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        throw new IllegalStateException("Cannot wrap ticket of type: " + ticket.getClass() + " with a ticket delegator");
    }

    public void setCipherExecutor(final CipherExecutor<byte[], byte[]> cipherExecutor) {
        this.cipherExecutor = cipherExecutor;
    }

    /**
     * Encode ticket id into a SHA-512.
     *
     * @param ticketId the ticket id
     * @return the ticket
     */
    protected String encodeTicketId(final String ticketId)  {
        if (this.cipherExecutor == null) {
            logger.trace("Ticket encryption is not enabled. Falling back to default behavior");
            return ticketId;
        }
        if (StringUtils.isBlank(ticketId)) {
            return ticketId;
        }

        return DigestUtils.sha512(ticketId);
    }

    /**
     * Encode ticket.
     *
     * @param ticket the ticket
     * @return the ticket
     */
    protected Ticket encodeTicket(final Ticket ticket)  {
        if (this.cipherExecutor == null) {
            logger.trace("Ticket encryption is not enabled. Falling back to default behavior");
            return ticket;
        }

        if (ticket == null) {
            logger.debug("Ticket passed is null and cannot be encoded");
            return null;
        }
        
        logger.info("Encoding [{}]", ticket);
        final byte[] encodedTicketObject = SerializationUtils.serializeAndEncodeObject(
                this.cipherExecutor, ticket);
        final String encodedTicketId = encodeTicketId(ticket.getId());
        final Ticket encodedTicket = new EncodedTicket(
                ByteSource.wrap(encodedTicketObject), 
                encodedTicketId);
        logger.info("Created [{}]", encodedTicket);
        return encodedTicket;
    }

    /**
     * Decode ticket.
     *
     * @param result the result
     * @return the ticket
     */
    protected Ticket decodeTicket(final Ticket result) {
        if (this.cipherExecutor == null) {
            logger.trace("Ticket encryption is not enabled. Falling back to default behavior");
            return result;
        }

        if (result == null) {
            logger.debug("Ticket passed is null and cannot be decoded");
            return null;
        }

        logger.info("Attempting to decode {}", result);
        final EncodedTicket encodedTicket = (EncodedTicket) result;

        final Ticket ticket = SerializationUtils.decodeAndSerializeObject(
                encodedTicket.getEncoded(), this.cipherExecutor, Ticket.class);
        logger.info("Decoded {}",  ticket);
        return ticket;
    }

    /**
     * Decode tickets.
     *
     * @param items the items
     * @return the set
     */
    protected Collection<Ticket> decodeTickets(final Collection<Ticket> items) {
        if (this.cipherExecutor == null) {
            logger.trace("Ticket encryption is not enabled. Falling back to default behavior");
            return items;
        }

        return items.stream().map(this::decodeTicket).collect(Collectors.toSet());
    }

    @Nullable
    public List<Pair<Class<? extends Ticket>, Constructor<? extends AbstractTicketDelegator>>> getTicketDelegators() {
        return ticketDelegators;
    }

    public void setTicketDelegators(@Nullable final List<Pair<Class<? extends Ticket>, Constructor<? extends AbstractTicketDelegator>>>
                                            ticketDelegators) {
        this.ticketDelegators = ticketDelegators;
    }

    /**
     * Common code to go over expired tickets and clean them up.
     **/
    protected final void cleanupTickets() {
        try {
            if (preCleanupTickets()) {
                logger.debug("Beginning ticket cleanup...");
                this.getTickets().stream()
                        .filter(ticket -> ticket.isExpired())
                        .forEach(ticket -> {
                            if (ticket instanceof TicketGrantingTicket) {
                                logger.debug("Cleaning up expired ticket-granting ticket [{}]", ticket.getId());
                                this.logoutManager.performLogout((TicketGrantingTicket) ticket);
                                deleteTicket(ticket.getId());
                            } else if (ticket instanceof ServiceTicket) {
                                logger.debug("Cleaning up expired service ticket or its derivative [{}]", ticket.getId());
                                deleteTicket(ticket.getId());
                            } else {
                                logger.warn("Unknown ticket type [{}]. Nothing to clean up.", ticket.getClass().getSimpleName());
                            }
                        });
            }
        } finally {
            postCleanupTickets();
        }

    }

    /**
     * Post cleanup tickets. This injection point is always executed
     * in a finally block regardless of whether cleanup actually happened.
     */
    protected void postCleanupTickets() {

    }

    /**
     * Pre cleanup tickets.
     *
     * @return true, if cleanup should proceed. false otherwise.
     */
    protected boolean preCleanupTickets() {
        return true;
    }

    /**
     * Schedule reloader job.
     */
    @PostConstruct
    protected void scheduleCleanerJob() {
        try {

            if (!cleanerEnabled && isCleanerSupported()) {
                logger.info("Ticket registry cleaner is disabled or is not supported by {}. No cleaner processes will be scheduled.",
                        this.getClass().getName());
                return;
            }

            logger.info("Preparing to schedule job to clean up after tickets...");
            final JobDetail job = JobBuilder.newJob(this.getClass())
                    .withIdentity(this.getClass().getSimpleName().concat(UUID.randomUUID().toString()))
                    .build();

            final Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(this.getClass().getSimpleName().concat(UUID.randomUUID().toString()))
                    .startAt(DateTimeUtils.dateOf(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(this.startDelay)))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(this.refreshInterval)
                            .repeatForever()).build();

            logger.debug("Scheduling {} job", this.getClass().getSimpleName());
            scheduler.scheduleJob(job, trigger);
            logger.info("{} will clean tickets every {} minutes",
                    this.getClass().getSimpleName(),
                    TimeUnit.SECONDS.toMinutes(this.refreshInterval));
        } catch (final Exception e){
            logger.warn(e.getMessage(), e);
        }

    }

    @Override
    public final void execute(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
            cleanupTickets();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Indicates whether the registry supports automatic ticket cleanup. 
     * Generally, a registry that is able to return a collection of available
     * tickets should be able to support the cleanup process. Default is <code>true</code>.
     *
     * @return true/false.
     */
    protected boolean isCleanerSupported() {
        return true;
    }
}
