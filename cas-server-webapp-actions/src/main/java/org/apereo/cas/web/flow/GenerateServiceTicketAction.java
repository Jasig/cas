package org.apereo.cas.web.flow;

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.CasProtocolConstants;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.authentication.AuthenticationResult;
import org.apereo.cas.authentication.AuthenticationResultBuilder;
import org.apereo.cas.authentication.DefaultAuthenticationSystemSupport;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.ticket.AbstractTicketException;
import org.apereo.cas.ticket.InvalidTicketException;
import org.apereo.cas.ticket.ServiceTicket;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Action to generate a service ticket for a given Ticket Granting Ticket and
 * Service.
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
@RefreshScope
@Component("generateServiceTicketAction")
public class GenerateServiceTicketAction extends AbstractAction {
    /** Instance of CentralAuthenticationService. */
    
    @Autowired
    @Qualifier("centralAuthenticationService")
    private CentralAuthenticationService centralAuthenticationService;

    
    @Autowired
    @Qualifier("defaultAuthenticationSystemSupport")
    private AuthenticationSystemSupport authenticationSystemSupport = new DefaultAuthenticationSystemSupport();

    @Autowired
    @Qualifier("defaultTicketRegistrySupport")
    private TicketRegistrySupport ticketRegistrySupport;

    @Override
    protected Event doExecute(final RequestContext context) {
        final Service service = WebUtils.getService(context);
        final String ticketGrantingTicket = WebUtils.getTicketGrantingTicketId(context);

        try {
            /**
             * In the initial primary authentication flow, credentials are cached and available.
             * Since they are authenticated as part of submission first, there is no need to doubly
             * authenticate and verify credentials.
             *
             * In subsequent authentication flows where a TGT is available and only an ST needs to be
             * created, there are no cached copies of the credential, since we do have a TGT available.
             * So we will simply grab the available authentication and produce the final result based on that.
             */
            final Authentication authentication = this.ticketRegistrySupport.getAuthenticationFrom(ticketGrantingTicket);
            if (authentication == null) {
                throw new InvalidTicketException(
                        new AuthenticationException("No authentication found for ticket " + ticketGrantingTicket), ticketGrantingTicket);
            }
            final AuthenticationResultBuilder authenticationResultBuilder = this.authenticationSystemSupport
                    .establishAuthenticationContextFromInitial(authentication);
            final AuthenticationResult authenticationResult = authenticationResultBuilder.build(service);

            final ServiceTicket serviceTicketId = this.centralAuthenticationService
                    .grantServiceTicket(ticketGrantingTicket, service, authenticationResult);
            WebUtils.putServiceTicketInRequestScope(context, serviceTicketId);
            return success();

        } catch (final AbstractTicketException e) {
            if (e instanceof InvalidTicketException) {
                this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicket);
            }
            if (isGatewayPresent(context)) {
                return result("gateway");
            }
            return newEvent(CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, e);
        }
    }

    public void setCentralAuthenticationService(final CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    public void setAuthenticationSystemSupport(final AuthenticationSystemSupport authenticationSystemSupport) {
        this.authenticationSystemSupport = authenticationSystemSupport;
    }

    public void setTicketRegistrySupport(final TicketRegistrySupport ticketRegistrySupport) {
        this.ticketRegistrySupport = ticketRegistrySupport;
    }

    /**
     * Checks if {@code gateway} is present in the request params.
     *
     * @param context the context
     * @return true, if gateway present
     */
    protected boolean isGatewayPresent(final RequestContext context) {
        return StringUtils.hasText(context.getExternalContext()
            .getRequestParameterMap().get(CasProtocolConstants.PARAMETER_GATEWAY));
    }

    /**
     * New event based on the id, which contains an error attribute referring to the exception occurred.
     *
     * @param id the id
     * @param error the error
     * @return the event
     */
    private Event newEvent(final String id, final Exception error) {
        return new Event(this, id, new LocalAttributeMap<>("error", error));
    }
}
