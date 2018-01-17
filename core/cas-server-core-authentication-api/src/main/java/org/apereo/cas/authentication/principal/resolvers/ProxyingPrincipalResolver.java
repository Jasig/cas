package org.apereo.cas.authentication.principal.resolvers;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.services.persondir.IPersonAttributeDao;
import lombok.ToString;

/**
 * Provides the most basic means of principal resolution by mapping
 * {@link Credential#getId()} onto
 * {@link Principal#getId()}.
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
@Slf4j
@ToString
public class ProxyingPrincipalResolver implements PrincipalResolver {

    private final PrincipalFactory principalFactory;

    public ProxyingPrincipalResolver() {
        this(new DefaultPrincipalFactory());
    }

    public ProxyingPrincipalResolver(final PrincipalFactory principalFactory) {
        this.principalFactory = principalFactory;
    }

    @Override
    public Principal resolve(final Credential credential, final Principal currentPrincipal, final AuthenticationHandler handler) {
        return this.principalFactory.createPrincipal(credential.getId());
    }

    @Override
    public boolean supports(final Credential credential) {
        return credential.getId() != null;
    }

    @Override
    public IPersonAttributeDao getAttributeRepository() {
        return null;
    }
}
