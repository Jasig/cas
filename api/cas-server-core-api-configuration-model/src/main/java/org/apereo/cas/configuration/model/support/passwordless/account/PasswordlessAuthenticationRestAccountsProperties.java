package org.apereo.cas.configuration.model.support.passwordless.account;

import org.apereo.cas.configuration.support.RequiresModule;
import org.apereo.cas.configuration.support.RestEndpointProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * This is {@link PasswordlessAuthenticationRestAccountsProperties}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@RequiresModule(name = "cas-server-support-passwordless")
@Getter
@Setter
@Accessors(chain = true)
public class PasswordlessAuthenticationRestAccountsProperties extends RestEndpointProperties {
    private static final long serialVersionUID = -8102345678378393382L;
}
