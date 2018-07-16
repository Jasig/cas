package org.apereo.cas.pm.web.flow.actions;

import lombok.RequiredArgsConstructor;
import lombok.val;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.pm.PasswordManagementService;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * This is {@link InitPasswordResetAction}, serves a as placeholder for extensions.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class InitPasswordResetAction extends AbstractAction {
    private final PasswordManagementService passwordManagementService;
    
    @Override
    protected Event doExecute(final RequestContext requestContext) {
        val token = requestContext.getFlowScope().getString("token");

        if (StringUtils.isBlank(token)) {
            LOGGER.error("Password reset token is missing");
            return error();
        }

        val username = passwordManagementService.parseToken(token);
        if (StringUtils.isBlank(username)) {
            LOGGER.error("Password reset token could not be verified");
            return error();
        }
        val c = new UsernamePasswordCredential();
        c.setUsername(username);
        WebUtils.putCredential(requestContext, c);
        return success();
    }
}
