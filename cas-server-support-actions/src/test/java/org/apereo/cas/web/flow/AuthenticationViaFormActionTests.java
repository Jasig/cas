package org.apereo.cas.web.flow;

import org.apereo.cas.AbstractCentralAuthenticationServiceTests;
import org.apereo.cas.authentication.AuthenticationResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.TestUtils;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.web.support.WebUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindException;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockRequestContext;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author Scott Battaglia
 * @since 3.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthenticationViaFormActionTests extends AbstractCentralAuthenticationServiceTests {

    @Resource(name="authenticationViaFormAction")
    private AuthenticationViaFormAction action;

    @Resource(name="warnCookieGenerator")
    private CookieGenerator warnCookieGenerator;


    @Test
    public void verifySuccessfulAuthenticationWithNoService() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockRequestContext context = new MockRequestContext();

        request.addParameter("username", "test");
        request.addParameter("password", "test");

        context.setExternalContext(new ServletExternalContext(
                new MockServletContext(), request, new MockHttpServletResponse()));
        final Credential c = org.apereo.cas.authentication.TestUtils.getCredentialsWithSameUsernameAndPassword();
        putCredentialInRequestScope(context, c);

        assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, this.action.doExecute(context).getId());
    }

    @Test
    public void verifySuccessfulAuthenticationWithNoServiceAndWarn()
        throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockRequestContext context = new MockRequestContext();


        request.addParameter("username", "test");
        request.addParameter("password", "test");
        request.addParameter("warn", "true");

        context.setExternalContext(new ServletExternalContext(
                new MockServletContext(), request, response));
        final Credential c = org.apereo.cas.authentication.TestUtils.getCredentialsWithSameUsernameAndPassword();
        putCredentialInRequestScope(context, c);

        assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, this.action.doExecute(context).getId());
        assertNotNull(WebUtils.getTicketGrantingTicketId(context));
        assertNotNull(response.getCookie(this.warnCookieGenerator.getCookieName()));
    }

    @Test
    public void verifySuccessfulAuthenticationWithServiceAndWarn() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockRequestContext context = new MockRequestContext();

        request.addParameter("username", "test");
        request.addParameter("password", "test");
        request.addParameter("warn", "true");
        request.addParameter("service", "test");

        context.setExternalContext(new ServletExternalContext(
                new MockServletContext(), request, response));
        final Credential c = org.apereo.cas.authentication.TestUtils.getCredentialsWithSameUsernameAndPassword();
        putCredentialInRequestScope(context, c);

        assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, this.action.doExecute(context).getId());
        assertNotNull(response.getCookie(this.warnCookieGenerator.getCookieName()));
    }

    @Test
    public void verifyFailedAuthenticationWithNoService() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockRequestContext context = new MockRequestContext();

        request.addParameter("username", "test");
        request.addParameter("password", "test2");

        context.setExternalContext(new ServletExternalContext(
                new MockServletContext(), request, new MockHttpServletResponse()));

        final Credential c = org.apereo.cas.authentication.TestUtils.getCredentialsWithDifferentUsernameAndPassword();
        putCredentialInRequestScope(context, c);

        context.getRequestScope().put(
            "org.springframework.validation.BindException.credentials",
            new BindException(c, "credential"));

        assertEquals(CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, this.action.doExecute(context).getId());
    }

    @Test
    public void verifyRenewWithServiceAndSameCredentials() throws Exception {
        final Credential c = org.apereo.cas.authentication.TestUtils.getCredentialsWithSameUsernameAndPassword();
        final Service service = TestUtils.getService(TestUtils.CONST_TEST_URL);
        final AuthenticationResult ctx = org.apereo.cas.authentication.TestUtils.getAuthenticationResult(
                getAuthenticationSystemSupport(), service, c);

        final TicketGrantingTicket ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockRequestContext context = new MockRequestContext();

        WebUtils.putTicketGrantingTicketInScopes(context, ticketGrantingTicket);

        request.addParameter("renew", "true");
        request.addParameter("service", TestUtils.getService(TestUtils.CONST_TEST_URL).getId());
        request.addParameter("username", "test");
        request.addParameter("password", "test");

        context.setExternalContext(new ServletExternalContext(
            new MockServletContext(), request, new MockHttpServletResponse()));
        context.getFlowScope().put("service", TestUtils.getService());

        assertEquals(CasWebflowConstants.TRANSITION_ID_WARN, this.action.doExecute(context).getId());
    }

    @Test
    public void verifyRenewWithServiceAndDifferentCredentials() throws Exception {
        final Credential c = org.apereo.cas.authentication.TestUtils.getCredentialsWithSameUsernameAndPassword();

        final AuthenticationResult ctx = org.apereo.cas.authentication.TestUtils.getAuthenticationResult(
                getAuthenticationSystemSupport(), TestUtils.getService("test"), c);

        final TicketGrantingTicket ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockRequestContext context = new MockRequestContext();

        WebUtils.putTicketGrantingTicketInScopes(context, ticketGrantingTicket);
        request.addParameter("renew", "true");
        request.addParameter("service", TestUtils.getService("test").getId());

        final Credential c2 = org.apereo.cas.authentication.TestUtils.getCredentialsWithSameUsernameAndPassword();
        context.setExternalContext(new ServletExternalContext(
            new MockServletContext(), request, new MockHttpServletResponse()));
        putCredentialInRequestScope(context, c2);

        assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, this.action.doExecute(context).getId());
    }

    @Test
    public void verifyRenewWithServiceAndBadCredentials() throws Exception {
        final Credential c = org.apereo.cas.authentication.TestUtils.getCredentialsWithSameUsernameAndPassword();
        final Service service = TestUtils.getService("test");
        final AuthenticationResult ctx = org.apereo.cas.authentication.TestUtils.getAuthenticationResult(
                getAuthenticationSystemSupport(), service, c);

        final TicketGrantingTicket ticketGrantingTicket = getCentralAuthenticationService().createTicketGrantingTicket(ctx);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockRequestContext context = new MockRequestContext();

        WebUtils.putTicketGrantingTicketInScopes(context, ticketGrantingTicket);
        request.addParameter("renew", "true");
        request.addParameter("service", service.getId());

        final Credential c2 = org.apereo.cas.authentication.TestUtils.getCredentialsWithDifferentUsernameAndPassword();
        context.setExternalContext(new ServletExternalContext(
            new MockServletContext(), request, new MockHttpServletResponse()));
        putCredentialInRequestScope(context, c2);
        assertEquals(CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, this.action.doExecute(context).getId());
    }


    /**
     * Put credentials in request scope.
     *
     * @param context the context
     * @param c the credential
     */
    private static void putCredentialInRequestScope(
            final RequestContext context, final Credential c) {
        context.getRequestScope().put("credential", c);
    }
}
