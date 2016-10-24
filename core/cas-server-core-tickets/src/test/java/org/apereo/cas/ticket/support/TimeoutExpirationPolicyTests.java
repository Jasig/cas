package org.apereo.cas.ticket.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apereo.cas.authentication.TestUtils;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketGrantingTicketImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class TimeoutExpirationPolicyTests {

    private static final File JSON_FILE = new File("timeoutExpirationPolicy.json");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final long TIMEOUT = 1;

    private ExpirationPolicy expirationPolicy;

    private Ticket ticket;

    @Before
    public void setUp() throws Exception {
        this.expirationPolicy = new TimeoutExpirationPolicy(TIMEOUT);

        this.ticket = new TicketGrantingTicketImpl("test", TestUtils
            .getAuthentication(), this.expirationPolicy);
    }

    @Test
    public void verifyTicketIsNull() {
        assertTrue(this.expirationPolicy.isExpired(null));
    }

    @Test
    public void verifyTicketIsNotExpired() {
        assertFalse(this.ticket.isExpired());
    }

    @Test
    public void verifyTicketIsExpired() throws InterruptedException {
        ticket = new TicketGrantingTicketImpl("test", TestUtils.getAuthentication(), new TimeoutExpirationPolicy(-100));
        assertTrue(ticket.isExpired());
    }

    @Test
    public void verifySerializeATimeoutExpirationPolicyToJson() throws IOException {
        mapper.writeValue(JSON_FILE, expirationPolicy);

        final ExpirationPolicy policyRead = mapper.readValue(JSON_FILE, TimeoutExpirationPolicy.class);

        assertEquals(expirationPolicy, policyRead);
    }
}
