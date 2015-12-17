package org.jasig.cas.web.flow;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.AuthenticationException;
import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.AbstractTicketException;
import org.jasig.cas.ticket.InvalidTicketException;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.validation.constraints.NotNull;

/**
 * Action to generate a service ticket for a given Ticket Granting Ticket and
 * Service.
 *
 * @author Scott Battaglia
 * @since 3.0.0.4
 */
@Component("generateServiceTicketAction")
public final class GenerateServiceTicketAction extends AbstractAction {

    /** Instance of CentralAuthenticationService. */
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    @Override
    protected Event doExecute(final RequestContext context) {
        final Service service = WebUtils.getService(context);
        final String ticketGrantingTicket = WebUtils.getTicketGrantingTicketId(context);

        try {
            final Credential credential = WebUtils.getCredential(context);

            final ServiceTicket serviceTicketId = this.centralAuthenticationService
                .grantServiceTicket(ticketGrantingTicket, service, credential);
            WebUtils.putServiceTicketInRequestScope(context, serviceTicketId);
            return success();
        } catch (final AuthenticationException e) {
            logger.error("Could not verify credentials to grant service ticket", e);
        } catch (final AbstractTicketException e) {
            if (e instanceof InvalidTicketException) {
                this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicket);
            }
            if (isGatewayPresent(context)) {
                return result("gateway");
            }
        }

        return error();
    }

    @Autowired
    public void setCentralAuthenticationService(
        @Qualifier("centralAuthenticationService")
        final CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    /**
     * Checks if {@code gateway} is present in the request params.
     *
     * @param context the context
     * @return true, if gateway present
     */
    protected boolean isGatewayPresent(final RequestContext context) {
        return StringUtils.hasText(context.getExternalContext()
            .getRequestParameterMap().get("gateway"));
    }
}
