package org.apereo.cas.support.pac4j.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.util.Pac4jUtils;
import org.apereo.cas.web.support.WebUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.saml.client.SAML2Client;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is {@link SAML2ClientLogoutAction}.
 * 
 * The action takes into account the currently used PAC4J client which is stored
 * in the user profile. If the client is not a SAML2 client, nothing happens. If
 * it is a SAML2 client, its logout action is executed.
 * 
 * Assumption: The PAC4J user profile should be in the user session during
 * logout, accessible with PAC4J Profile Manager. The Logout web flow should
 * make sure the user profile is present.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
public class SAML2ClientLogoutAction extends AbstractAction {

    private final Clients clients;

    public SAML2ClientLogoutAction(final Clients clients) {
        this.clients = clients;
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        try {
            final HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
            final HttpServletResponse response = WebUtils.getHttpServletResponseFromExternalWebflowContext(requestContext);
            final J2EContext context = Pac4jUtils.getPac4jJ2EContext(request, response);

            Client<?, ?> client;
            try {
                final String currentClientName = Pac4jUtils.findCurrentClientName(context);
                client = (currentClientName == null) ? null : clients.findClient(currentClientName);
            } catch(final TechnicalException e) {
                // this exception indicates that the SAML2Client is not in the list
                LOGGER.debug("No SAML2 client found");
                client = null;
            }

            // Call logout on SAML2 clients only
            if (client instanceof SAML2Client) {
                final SAML2Client saml2Client = (SAML2Client) client;
                LOGGER.debug("Located SAML2 client [{}]", saml2Client);
                final RedirectAction action = saml2Client.getLogoutAction(context, null, null);
                LOGGER.debug("Preparing logout message to send is [{}]", action.getLocation());
                action.perform(context);
            } else {
                LOGGER.debug("The current client is not a SAML2 client or it cannot be found at all, no logout action will be executed.");
            }
        } catch (final Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

}
