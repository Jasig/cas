package org.apereo.cas.support.oauth.authenticator;

import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.ticket.registry.TicketRegistry;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;

import java.util.HashMap;

/**
 * This is {@link OAuth20AccessTokenAuthenticator}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Slf4j
@RequiredArgsConstructor
public class OAuth20AccessTokenAuthenticator implements Authenticator<TokenCredentials> {
    private final TicketRegistry ticketRegistry;

    @SneakyThrows
    @Override
    public void validate(final TokenCredentials tokenCredentials, final WebContext webContext) {
        val token = tokenCredentials.getToken();
        LOGGER.trace("Received access token [{}] for authentication", token);
        
        val accessToken = ticketRegistry.getTicket(token, AccessToken.class);
        if (accessToken == null || accessToken.isExpired()) {
            LOGGER.error("Provided access token [{}] is either not found in the ticket registry or has expired", token);
            return;
        }
        val profile = buildUserProfile(tokenCredentials, webContext, accessToken);
        if (profile != null) {
            LOGGER.trace("Final user profile based on access token [{}] is [{}]", accessToken, profile);
            tokenCredentials.setUserProfile(profile);
        }
    }

    /**
     * Build user profile common profile.
     *
     * @param tokenCredentials the token credentials
     * @param webContext       the web context
     * @param accessToken      the access token
     * @return the common profile
     */
    protected CommonProfile buildUserProfile(final TokenCredentials tokenCredentials,
                                             final WebContext webContext,
                                             final AccessToken accessToken) {
        val userProfile = new CommonProfile(true);
        val authentication = accessToken.getAuthentication();
        val principal = authentication.getPrincipal();

        userProfile.setId(principal.getId());

        val attributes = new HashMap<String, Object>(principal.getAttributes());
        attributes.putAll(authentication.getAttributes());
        userProfile.addAttributes(attributes);

        LOGGER.trace("Built user profile based on access token [{}] is [{}]", accessToken, userProfile);
        return userProfile;
    }
}
