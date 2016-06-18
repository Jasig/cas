package org.apereo.cas.web.support;

import org.apereo.inspektr.common.web.ClientInfoHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * Throttles access attempts for failed logins by IP Address. This stores the attempts in memory.
 * This is not good for a clustered environment unless the intended behavior is that this blocking is per-machine.
 *
 * @author Scott Battaglia
 * @since 3.3.5
 */
public class InMemoryThrottledSubmissionByIpAddressHandlerInterceptorAdapter
             extends AbstractInMemoryThrottledSubmissionHandlerInterceptorAdapter {
    
    @Override
    protected String constructKey(final HttpServletRequest request) {
        return ClientInfoHolder.getClientInfo().getClientIpAddress();
    }

    @Override
    protected String getName() {
        return "inMemoryIpAddressThrottle";
    }
}
