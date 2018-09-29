package org.apereo.cas.web.flow.mfa;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.MultifactorAuthenticationUtils;
import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.services.MultifactorAuthenticationProvider;
import org.apereo.cas.util.spring.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Abstract class that provides the doPreExecute() hook to set the find the provider for this webflow to be used by
 * extending classes in doExecute().
 *
 * @author Travis Schmidt
 * @since 5.3.4
 */
@Slf4j
public abstract class AbstractMultifactorAuthenticationAction<T extends MultifactorAuthenticationProvider> extends AbstractAction {

    /**
     * The resolved provider for this flow.
     */
    protected transient T provider;

    @Override
    protected Event doPreExecute(final RequestContext requestContext) throws Exception {
        final String flowId = requestContext.getActiveFlow().getId();
        final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        final MultifactorAuthenticationProvider provider =
                MultifactorAuthenticationUtils.getMultifactorAuthenticationProviderById(flowId, applicationContext)
                        .orElseThrow(AuthenticationException::new);

        return null;
    }
}
