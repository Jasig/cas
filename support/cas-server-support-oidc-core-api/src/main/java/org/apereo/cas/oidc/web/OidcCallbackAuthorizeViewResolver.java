package org.apereo.cas.oidc.web;

import org.apereo.cas.oidc.OidcConstants;
import org.apereo.cas.oidc.util.OidcAuthorizationRequestSupport;
import org.apereo.cas.support.oauth.web.views.OAuth20CallbackAuthorizeViewResolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.ProfileManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import static org.apereo.cas.oidc.util.OidcAuthorizationRequestSupport.getRedirectUrlWithError;

/**
 * This is {@link OidcCallbackAuthorizeViewResolver}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@RequiredArgsConstructor
@Slf4j
public class OidcCallbackAuthorizeViewResolver implements OAuth20CallbackAuthorizeViewResolver {
    @Override
    public ModelAndView resolve(final JEEContext ctx, final ProfileManager manager, final String url) {
        val prompt = OidcAuthorizationRequestSupport.getOidcPromptFromAuthorizationRequest(url);
        if (prompt.contains(OidcConstants.PROMPT_NONE)) {
            val result = manager.get(true);
            if (result.isPresent()) {
                LOGGER.trace("Redirecting to URL [{}] without prompting for login", url);
                return new ModelAndView(url);
            }
            LOGGER.warn("Unable to detect an authenticated user profile for prompt-less login attempts");
            return new ModelAndView(new RedirectView(getRedirectUrlWithError(ctx.getFullRequestURL(), OidcConstants.LOGIN_REQUIRED)));
        }
        if (prompt.contains(OidcConstants.PROMPT_LOGIN)) {
            LOGGER.trace("Removing login prompt from URL [{}]", url);
            val newUrl = OidcAuthorizationRequestSupport.removeOidcPromptFromAuthorizationRequest(url, OidcConstants.PROMPT_LOGIN);
            LOGGER.trace("Redirecting to URL [{}]", newUrl);
            return new ModelAndView(new RedirectView(newUrl));
        }
        LOGGER.trace("Redirecting to URL [{}]", url);
        return new ModelAndView(new RedirectView(url));
    }

}
