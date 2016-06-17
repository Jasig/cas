package org.apereo.cas.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is {@link DefaultAuthenticationTransactionManager}.
 *
 * @author Misagh Moayyed
 * @since 4.2.0
 */
public class DefaultAuthenticationTransactionManager implements AuthenticationTransactionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuthenticationTransactionManager.class);
    
    private AuthenticationManager authenticationManager;

    public DefaultAuthenticationTransactionManager() {
    }

    public DefaultAuthenticationTransactionManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationTransactionManager handle(final AuthenticationTransaction authenticationTransaction,
                                                   final AuthenticationResultBuilder authenticationResult)
                                                    throws AuthenticationException {
        if (!authenticationTransaction.getCredentials().isEmpty()) {
            final Authentication authentication = this.authenticationManager.authenticate(authenticationTransaction);
            LOGGER.debug("Successful authentication; Collecting authentication result [{}]", authentication);
            authenticationResult.collect(authentication);
        } else {
            LOGGER.debug("Transaction ignored since there are no credentials to authenticate");
        }
        return this;
    }

    /**
     * Sets authentication manager.
     *
     * @param authenticationManager the authentication manager
     */
    @Override
    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

}
