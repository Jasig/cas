package org.apereo.cas.ticket;

import static org.junit.Assert.*;

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.TestUtils;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.ticket.support.MultiTimeUseOrTimeoutExpirationPolicy;
import org.apereo.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.apereo.cas.util.DefaultUniqueTicketIdGenerator;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class ServiceTicketImplTests {

    private TicketGrantingTicketImpl ticketGrantingTicket = new TicketGrantingTicketImpl("test",
            TestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());

    private UniqueTicketIdGenerator uniqueTicketIdGenerator = new DefaultUniqueTicketIdGenerator();

    @Test(expected = Exception.class)
    public void verifyNoService() {
        new ServiceTicketImpl("stest1", this.ticketGrantingTicket, null, true, new NeverExpiresExpirationPolicy());
    }

    @Test(expected = Exception.class)
    public void verifyNoTicket() {
        new ServiceTicketImpl("stest1", null, org.apereo.cas.services.TestUtils.getService(), true, new NeverExpiresExpirationPolicy());
    }

    @Test
    public void verifyIsFromNewLoginTrue() {
        final ServiceTicket s = new ServiceTicketImpl("stest1", this.ticketGrantingTicket,
                org.apereo.cas.services.TestUtils.getService(), true,
                new NeverExpiresExpirationPolicy());
        assertTrue(s.isFromNewLogin());
    }

    @Test
    public void verifyIsFromNewLoginFalse() {
        final ServiceTicket s = new ServiceTicketImpl("stest1", this.ticketGrantingTicket,
                org.apereo.cas.services.TestUtils.getService(), false,
                new NeverExpiresExpirationPolicy());
        assertFalse(s.isFromNewLogin());
    }

    @Test
    public void verifyGetService() {
        final Service simpleService = org.apereo.cas.services.TestUtils.getService();
        final ServiceTicket s = new ServiceTicketImpl("stest1", this.ticketGrantingTicket, simpleService, false,
                new NeverExpiresExpirationPolicy());
        Assert.assertEquals(simpleService, s.getService());
    }

    @Test
    public void verifyGetTicket() {
        final Service simpleService = org.apereo.cas.services.TestUtils.getService();
        final ServiceTicket s = new ServiceTicketImpl("stest1", this.ticketGrantingTicket, simpleService, false,
                new NeverExpiresExpirationPolicy());
        assertEquals(this.ticketGrantingTicket, s.getGrantingTicket());
    }

    @Test
    public void verifyIsExpiredTrueBecauseOfRoot() {
        final TicketGrantingTicket t = new TicketGrantingTicketImpl("test",
                TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy());
        final ServiceTicket s = t.grantServiceTicket(this.uniqueTicketIdGenerator.getNewTicketId(ServiceTicket.PREFIX),
                org.apereo.cas.services.TestUtils.getService(), new NeverExpiresExpirationPolicy(), false, true);
        t.markTicketExpired();

        assertTrue(s.isExpired());
    }

    @Test
    public void verifyIsExpiredFalse() {
        final TicketGrantingTicket t = new TicketGrantingTicketImpl("test",
                TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy());
        final ServiceTicket s = t.grantServiceTicket(this.uniqueTicketIdGenerator.getNewTicketId(ServiceTicket.PREFIX),
                org.apereo.cas.services.TestUtils.getService(),
                new MultiTimeUseOrTimeoutExpirationPolicy(1, 5000), false, true);
        assertFalse(s.isExpired());
    }

    @Test
    public void verifyTicketGrantingTicket() throws AbstractTicketException {
        final Authentication a = TestUtils.getAuthentication();
        final TicketGrantingTicket t = new TicketGrantingTicketImpl("test",
                TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy());
        final ServiceTicket s = t.grantServiceTicket(this.uniqueTicketIdGenerator.getNewTicketId(ServiceTicket.PREFIX),
                org.apereo.cas.services.TestUtils.getService(),
                new MultiTimeUseOrTimeoutExpirationPolicy(1, 5000), false, true);
        final TicketGrantingTicket t1 = s.grantProxyGrantingTicket(
                this.uniqueTicketIdGenerator.getNewTicketId(TicketGrantingTicket.PREFIX), a,
                new NeverExpiresExpirationPolicy());

        Assert.assertEquals(a, t1.getAuthentication());
    }

    @Test
    public void verifyTicketGrantingTicketGrantedTwice() throws AbstractTicketException  {
        final Authentication a = TestUtils.getAuthentication();
        final TicketGrantingTicket t = new TicketGrantingTicketImpl("test",
                TestUtils.getAuthentication(),
                new NeverExpiresExpirationPolicy());
        final ServiceTicket s = t.grantServiceTicket(this.uniqueTicketIdGenerator.getNewTicketId(ServiceTicket.PREFIX),
                org.apereo.cas.services.TestUtils.getService(),
                new MultiTimeUseOrTimeoutExpirationPolicy(1, 5000), false, true);
        s.grantProxyGrantingTicket(this.uniqueTicketIdGenerator.getNewTicketId(TicketGrantingTicket.PREFIX), a,
                new NeverExpiresExpirationPolicy());

        try {
            s.grantProxyGrantingTicket(this.uniqueTicketIdGenerator.getNewTicketId(TicketGrantingTicket.PREFIX), a,
                    new NeverExpiresExpirationPolicy());
            fail("Exception expected.");
        } catch (final Exception e) {
            return;
        }
    }
}
