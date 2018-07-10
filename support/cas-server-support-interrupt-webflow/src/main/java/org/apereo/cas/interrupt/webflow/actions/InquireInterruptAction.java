package org.apereo.cas.interrupt.webflow.actions;

import lombok.val;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.interrupt.InterruptInquirer;
import org.apereo.cas.interrupt.webflow.InterruptUtils;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * This is {@link InquireInterruptAction}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
@RequiredArgsConstructor
public class InquireInterruptAction extends AbstractAction {
    private final InterruptInquirer interruptInquirer;

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        val authentication = WebUtils.getAuthentication(requestContext);
        val service = WebUtils.getService(requestContext);
        val registeredService = WebUtils.getRegisteredService(requestContext);
        val credential = WebUtils.getCredential(requestContext);

        val response = this.interruptInquirer.inquire(authentication, registeredService, service, credential);
        if (response == null || !response.isInterrupt()) {
            return no();
        }
        InterruptUtils.putInterruptIn(requestContext, response);
        WebUtils.putPrincipal(requestContext, authentication.getPrincipal());
        return yes();
    }
}
