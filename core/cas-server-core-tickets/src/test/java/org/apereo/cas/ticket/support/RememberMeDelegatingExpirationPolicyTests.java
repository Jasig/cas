package org.apereo.cas.ticket.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.RememberMeCredential;
import org.apereo.cas.authentication.TestUtils;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.TicketGrantingTicketImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for RememberMeDelegatingExpirationPolicy.
 *
 * @author Scott Battaglia
 * @since 3.2.1
 *
 */
public class RememberMeDelegatingExpirationPolicyTests {

    private static final File JSON_FILE = new File("rememberMeDelegatingExpirationPolicy.json");
    private static final ObjectMapper mapper = new ObjectMapper();

    /** Factory to create the principal type. **/
    protected PrincipalFactory principalFactory = new DefaultPrincipalFactory();

    private RememberMeDelegatingExpirationPolicy p;

    @Before
    public void setUp() throws Exception {
        this.p = new RememberMeDelegatingExpirationPolicy();
        this.p.setRememberMeExpirationPolicy(new MultiTimeUseOrTimeoutExpirationPolicy(1, 20000));
        this.p.setSessionExpirationPolicy(new MultiTimeUseOrTimeoutExpirationPolicy(5, 20000));
    }

    @Test
    public void verifyTicketExpirationWithRememberMe() {
        final Authentication authentication = TestUtils.getAuthentication(
                this.principalFactory.createPrincipal("test"),
                Collections.<String, Object>singletonMap(
                        RememberMeCredential.AUTHENTICATION_ATTRIBUTE_REMEMBER_ME, true));
        final TicketGrantingTicketImpl t = new TicketGrantingTicketImpl("111", authentication, this.p);
        assertFalse(t.isExpired());
        t.grantServiceTicket("55", org.apereo.cas.services.TestUtils.getService(), this.p, false, true);
        assertTrue(t.isExpired());
    }

    @Test
    public void verifyTicketExpirationWithoutRememberMe() {
        final Authentication authentication = TestUtils.getAuthentication();
        final TicketGrantingTicketImpl t = new TicketGrantingTicketImpl("111", authentication, this.p);
        assertFalse(t.isExpired());
        t.grantServiceTicket("55", org.apereo.cas.services.TestUtils.getService(), this.p, false, true);
        assertFalse(t.isExpired());
    }

    @Test
    public void verifySerializeATimeoutExpirationPolicyToJson() throws IOException {
        mapper.writeValue(JSON_FILE, p);

        final ExpirationPolicy policyRead = mapper.readValue(JSON_FILE, RememberMeDelegatingExpirationPolicy.class);

        assertEquals(p, policyRead);
    }
}
