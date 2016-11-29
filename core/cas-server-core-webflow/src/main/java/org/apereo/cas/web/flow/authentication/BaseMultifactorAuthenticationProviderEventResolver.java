package org.apereo.cas.web.flow.authentication;

import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.MultifactorAuthenticationProvider;
import org.apereo.cas.services.MultifactorAuthenticationProviderResolver;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceAccessStrategyUtils;
import org.apereo.cas.services.VariegatedMultifactorAuthenticationProvider;
import org.apereo.cas.web.flow.resolver.impl.AbstractCasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * This is {@link BaseMultifactorAuthenticationProviderEventResolver}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public abstract class BaseMultifactorAuthenticationProviderEventResolver extends AbstractCasWebflowEventResolver
        implements MultifactorAuthenticationProviderResolver {

    @Override
    public Optional<MultifactorAuthenticationProvider> resolveProvider(final Map<String, MultifactorAuthenticationProvider> providers,
                                                                       final Collection<String> requestMfaMethod) {
        final Optional<MultifactorAuthenticationProvider> providerFound = providers.values().stream()
                .filter(p -> requestMfaMethod.stream().anyMatch(p::matches))
                .findFirst();
        if (providerFound.isPresent()) {
            final MultifactorAuthenticationProvider provider = providerFound.get();
            if (provider instanceof VariegatedMultifactorAuthenticationProvider) {
                final VariegatedMultifactorAuthenticationProvider multi = VariegatedMultifactorAuthenticationProvider.class.cast(provider);
                return multi.getProviders().stream()
                        .filter(p -> requestMfaMethod.stream().anyMatch(p::matches))
                        .findFirst();
            }
        }

        return providerFound;
    }

    /**
     * Locate the provider in the collection, and have it match the requested mfa.
     * If the provider is multi-instance, resolve based on inner-registered providers.
     *
     * @param providers        the providers
     * @param requestMfaMethod the request mfa method
     * @return the optional
     */
    public Optional<MultifactorAuthenticationProvider> resolveProvider(final Map<String, MultifactorAuthenticationProvider> providers,
                                                                       final String... requestMfaMethod) {
        return resolveProvider(providers, new HashSet<>(Arrays.asList(requestMfaMethod)));
    }

    /**
     * Locate the provider in the collection, and have it match the requested mfa.
     * If the provider is multi-instance, resolve based on inner-registered providers.
     *
     * @param providers        the providers
     * @param requestMfaMethod the request mfa method
     * @return the optional
     */
    public Optional<MultifactorAuthenticationProvider> resolveProvider(final Map<String, MultifactorAuthenticationProvider> providers,
                                                                       final String requestMfaMethod) {
        return resolveProvider(providers, new HashSet<>(Collections.singletonList(requestMfaMethod)));
    }

    @Override
    public Collection<MultifactorAuthenticationProvider> flattenProviders(final Collection<? extends MultifactorAuthenticationProvider> providers) {
        final Collection<MultifactorAuthenticationProvider> flattenedProviders = new HashSet<>();
        providers.forEach(p -> {
            if (p instanceof VariegatedMultifactorAuthenticationProvider) {
                flattenedProviders.addAll(VariegatedMultifactorAuthenticationProvider.class.cast(p).getProviders());
            } else {
                flattenedProviders.add(p);
            }
        });

        return flattenedProviders;
    }

    /**
     * Resolve registered service in request context.
     *
     * @param requestContext the request context
     * @return the registered service
     */
    protected RegisteredService resolveRegisteredServiceInRequestContext(final RequestContext requestContext) {
        final Service ctxService = WebUtils.getService(requestContext);
        final Service resolvedService = resolveServiceFromAuthenticationRequest(ctxService);
        final RegisteredService service = this.servicesManager.findServiceBy(resolvedService);
        RegisteredServiceAccessStrategyUtils.ensureServiceAccessIsAllowed(resolvedService, service);
        return service;
    }
}
