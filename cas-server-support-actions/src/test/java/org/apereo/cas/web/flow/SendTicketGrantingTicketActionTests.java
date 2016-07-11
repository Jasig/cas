package org.apereo.cas.web.flow;

import org.apereo.cas.AbstractCentralAuthenticationServiceTests;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.config.CasSupportActionsConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.apereo.cas.CasProtocolConstants;
import org.apereo.cas.web.support.WebUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.test.MockRequestContext;

import javax.servlet.http.Cookie;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Marvin S. Addison
 * @since 3.4.0
 */
@SpringApplicationConfiguration(classes = {CasSupportActionsConfiguration.class,
        CasCoreWebflowConfiguration.class, CasCookieConfiguration.class})
public class SendTicketGrantingTicketActionTests extends AbstractCentralAuthenticationServiceTests {
    private SendTicketGrantingTicketAction action;
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;
    private MockRequestContext context;

    @Before
    public void onSetUp() throws Exception {

        this.ticketGrantingTicketCookieGenerator = new CookieRetrievingCookieGenerator();
        ticketGrantingTicketCookieGenerator.setCookieName("TGT");

        this.action = new SendTicketGrantingTicketAction();
        this.action.setCentralAuthenticationService(getCentralAuthenticationService());
        this.action.setTicketGrantingTicketCookieGenerator(ticketGrantingTicketCookieGenerator);
        this.action.setServicesManager(getServicesManager());

        this.action.setCreateSsoSessionCookieOnRenewAuthentications(true);
        this.action.afterPropertiesSet();

        this.context = new MockRequestContext();
    }

    @Test
    public void verifyNoTgtToSet() throws Exception {
        this.context.setExternalContext(new ServletExternalContext(new MockServletContext(),
                new MockHttpServletRequest(), new MockHttpServletResponse()));

        assertEquals("success", this.action.execute(this.context).getId());
    }

    @Test
    public void verifyTgtToSet() throws Exception {
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final TicketGrantingTicket tgt = mock(TicketGrantingTicket.class);
        when(tgt.getId()).thenReturn("test");

        WebUtils.putTicketGrantingTicketInScopes(this.context, tgt);
        this.context.setExternalContext(new ServletExternalContext(new MockServletContext(),
                request, response));

        assertEquals("success", this.action.execute(this.context).getId());
        request.setCookies(response.getCookies());
        assertEquals(tgt.getId(), this.ticketGrantingTicketCookieGenerator.retrieveCookieValue(request));
    }

    @Test
    public void verifyTgtToSetRemovingOldTgt() throws Exception {
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockHttpServletRequest request = new MockHttpServletRequest();

        final TicketGrantingTicket tgt = mock(TicketGrantingTicket.class);
        when(tgt.getId()).thenReturn("test");

        request.setCookies(new Cookie("TGT", "test5"));
        WebUtils.putTicketGrantingTicketInScopes(this.context, tgt);
        this.context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, response));

        assertEquals("success", this.action.execute(this.context).getId());
        request.setCookies(response.getCookies());
        assertEquals(tgt.getId(), this.ticketGrantingTicketCookieGenerator.retrieveCookieValue(request));
    }

    @Test
    public void verifySsoSessionCookieOnRenewAsParameter() throws Exception {
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(CasProtocolConstants.PARAMETER_RENEW, "true");

        final TicketGrantingTicket tgt = mock(TicketGrantingTicket.class);
        when(tgt.getId()).thenReturn("test");
        request.setCookies(new Cookie("TGT", "test5"));
        WebUtils.putTicketGrantingTicketInScopes(this.context, tgt);
        this.context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, response));

        this.action.setCreateSsoSessionCookieOnRenewAuthentications(false);
        assertEquals("success", this.action.execute(this.context).getId());
        assertEquals(0, response.getCookies().length);
    }

    @Test
    public void verifySsoSessionCookieOnServiceSsoDisallowed() throws Exception {
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockHttpServletRequest request = new MockHttpServletRequest();

        final WebApplicationService svc = mock(WebApplicationService.class);
        when(svc.getId()).thenReturn("TestSsoFalse");

        final TicketGrantingTicket tgt = mock(TicketGrantingTicket.class);
        when(tgt.getId()).thenReturn("test");
        request.setCookies(new Cookie("TGT", "test5"));
        WebUtils.putTicketGrantingTicketInScopes(this.context, tgt);
        this.context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, response));
        this.context.getFlowScope().put("service", svc);
        this.action.setCreateSsoSessionCookieOnRenewAuthentications(false);
        assertEquals("success", this.action.execute(this.context).getId());
        assertEquals(0, response.getCookies().length);
    }
}
