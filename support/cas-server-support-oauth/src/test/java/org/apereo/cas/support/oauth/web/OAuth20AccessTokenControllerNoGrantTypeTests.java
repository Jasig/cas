package org.apereo.cas.support.oauth.web;

import org.apereo.cas.support.oauth.OAuth20GrantTypes;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.support.oauth.web.endpoints.OAuth20AccessTokenEndpointController;

import java.util.HashSet;

/**
 * This class tests the {@link OAuth20AccessTokenEndpointController} class.
 *
 * It does almost the same as {@link OAuth20AccessTokenControllerTests}, but the registered
 * services always contain empty allowed grant types. These tests are run to ensure that
 * the change that adds proper support for supportedGrantTypes does not break existing CAS
 * setups that does not specify allowedGrantTypes. Briefly, it checks that empty supportedGrantTypes
 * is equivalent to supportedGrantTypes with all valid values.
 * This behavior is probably going to change it 6.0 so this suite should be removed.
 *
 * @author Kirill Gagarski
 * @since 5.3.4
 */
public class OAuth20AccessTokenControllerNoGrantTypeTests extends OAuth20AccessTokenControllerTests {
    @Override
    protected OAuthRegisteredService getRegisteredService(final String serviceId, final String secret,
                                                          final HashSet<OAuth20GrantTypes> grantTypes) {
        final OAuthRegisteredService service = super.getRegisteredService(serviceId, secret, grantTypes);
        // no supportedGrantTypes == all grant types are supported
        service.setSupportedGrantTypes(new HashSet<>());
        return service;
    }

    @Override
    public void verifyClientDisallowedAuthorizationCode() {
        // This test should never fail in this suite, so just doing nothing.
    }

    // All the other tests should pass with overrides in this class.
}
