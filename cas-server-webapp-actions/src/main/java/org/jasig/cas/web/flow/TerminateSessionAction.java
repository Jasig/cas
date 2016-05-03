package org.jasig.cas.web.flow;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.AuthenticationSystemSupport;
import org.jasig.cas.authentication.DefaultAuthenticationSystemSupport;
import org.jasig.cas.logout.LogoutRequest;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.jasig.cas.web.support.WebUtils;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.ProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Terminates the CAS SSO session by destroying all SSO state data (i.e. TGT, cookies).
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
@RefreshScope
@Component("terminateSessionAction")
public class TerminateSessionAction {

    /** Webflow event helper component. */
    private EventFactorySupport eventFactorySupport = new EventFactorySupport();

    /** The CORE to which we delegate for all CAS functionality. */
    
    @Autowired
    @Qualifier("centralAuthenticationService")
    private CentralAuthenticationService centralAuthenticationService;

    /** CookieGenerator for TGT Cookie. */
    
    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    /** CookieGenerator for Warn Cookie. */
    
    @Autowired
    @Qualifier("warnCookieGenerator")
    private CookieRetrievingCookieGenerator warnCookieGenerator;

    
    @Autowired(required=false)
    @Qualifier("defaultAuthenticationSystemSupport")
    private AuthenticationSystemSupport authenticationSystemSupport = new DefaultAuthenticationSystemSupport();

    /**
     * Creates a new instance with the given parameters.
     */
    public TerminateSessionAction() {}

    /**
     * Terminates the CAS SSO session by destroying the TGT (if any) and removing cookies related to the SSO session.
     *
     * @param context Request context.
     *
     * @return "success"
     */
    public Event terminate(final RequestContext context) {
        // in login's webflow : we can get the value from context as it has already been stored
        String tgtId = WebUtils.getTicketGrantingTicketId(context);
        // for logout, we need to get the cookie's value
        if (tgtId == null) {
            final HttpServletRequest request = WebUtils.getHttpServletRequest(context);
            tgtId = this.ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
        }
        if (tgtId != null) {
            final List<LogoutRequest> logoutRequests = this.centralAuthenticationService.destroyTicketGrantingTicket(tgtId);
            WebUtils.putLogoutRequests(context, logoutRequests);
        }
        final HttpServletResponse response = WebUtils.getHttpServletResponse(context);
        final HttpServletRequest request = WebUtils.getHttpServletRequest(context);
        
        this.ticketGrantingTicketCookieGenerator.removeCookie(response);
        this.warnCookieGenerator.removeCookie(response);

        final J2EContext j2EContext = new J2EContext(request, response);
        final ProfileManager manager = new ProfileManager(j2EContext);
        manager.logout();
        
        return this.eventFactorySupport.success(this);
    }
}
