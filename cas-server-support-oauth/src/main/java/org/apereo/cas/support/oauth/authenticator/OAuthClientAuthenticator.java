package org.apereo.cas.support.oauth.authenticator;

import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.support.oauth.util.OAuthUtils;
import org.apereo.cas.support.oauth.validator.OAuthValidator;
import org.apereo.cas.support.oauth.profile.OAuthClientProfile;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;

/**
 * Authenticator for client credentials authentication.
 *
 * @author Jerome Leleu
 * @since 5.0.0
 */
public class OAuthClientAuthenticator implements UsernamePasswordAuthenticator {
    
    private OAuthValidator validator;
    
    private ServicesManager servicesManager;

    @Override
    public void validate(final UsernamePasswordCredentials credentials) {
        final String id = credentials.getUsername();
        final String secret = credentials.getPassword();
        final OAuthRegisteredService registeredService = OAuthUtils.getRegisteredOAuthService(this.servicesManager, id);

        if (!this.validator.checkServiceValid(registeredService)) {
            throw new CredentialsException("Service invalid for client identifier: " + id);
        }

        if (!this.validator.checkClientSecret(registeredService, secret)) {
            throw new CredentialsException("Bad secret for client identifier: " + id);
        }

        final OAuthClientProfile profile = new OAuthClientProfile();
        profile.setId(id);
        credentials.setUserProfile(profile);
    }

    public OAuthValidator getValidator() {
        return this.validator;
    }

    public void setValidator(final OAuthValidator validator) {
        this.validator = validator;
    }

    public ServicesManager getServicesManager() {
        return this.servicesManager;
    }

    public void setServicesManager(final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }
}
