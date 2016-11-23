package org.apereo.cas.web.flow.resolver.impl;

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.authentication.adaptive.geo.GeoLocationRequest;
import org.apereo.cas.authentication.adaptive.geo.GeoLocationResponse;
import org.apereo.cas.authentication.adaptive.geo.GeoLocationService;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.MultifactorAuthenticationProvider;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.web.flow.authentication.BaseMultifactorAuthenticationProviderEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.apereo.inspektr.audit.annotation.Audit;
import org.apereo.inspektr.common.web.ClientInfo;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This is {@link AdaptiveMultifactorAuthenticationPolicyEventResolver},
 * which handles the initial authentication attempt and calls upon a number of
 * embedded resolvers to produce the next event in the authentication flow.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class AdaptiveMultifactorAuthenticationPolicyEventResolver extends BaseMultifactorAuthenticationProviderEventResolver {

    private GeoLocationService geoLocationService;
    
    @Autowired
    private CasConfigurationProperties casProperties;
    
    @Override
    public Set<Event> resolveInternal(final RequestContext context) {
        final RegisteredService service = resolveRegisteredServiceInRequestContext(context);
        final Authentication authentication = WebUtils.getAuthentication(context);

        if (service == null || authentication == null) {
            logger.debug("No service or authentication is available to determine event for principal");
            return null;
        }
        
        final Map multifactorMap = casProperties.getAuthn().getAdaptive().getRequireMultifactor();
        if (multifactorMap == null || multifactorMap.isEmpty()) {
            logger.debug("Adaptive authentication is not configured to require multifactor authentication");
            return null;
        }
        
        final Map<String, MultifactorAuthenticationProvider> providerMap = 
                WebUtils.getAllMultifactorAuthenticationProviders(this.applicationContext);
        if (providerMap == null || providerMap.isEmpty()) {
            logger.error("No multifactor authentication providers are available in the application context");
            throw new AuthenticationException();
        }
        
        final ClientInfo clientInfo = ClientInfoHolder.getClientInfo();
        final String clientIp = clientInfo.getClientIpAddress();
        logger.debug("Located client IP address as [{}]", clientIp);

        final String agent = WebUtils.getHttpServletRequestUserAgent();

        final Set<Map.Entry> entries = multifactorMap.entrySet();
        for (final Map.Entry entry : entries) {
            final String mfaMethod = entry.getKey().toString();
            final String pattern = entry.getValue().toString();

            final Optional<MultifactorAuthenticationProvider> providerFound = resolveProvider(providerMap, mfaMethod);

            if (!providerFound.isPresent()) {
                logger.error("Adaptive authentication is configured to require [{}] for [{}], yet [{}] is absent in the configuration.",
                            mfaMethod, pattern, mfaMethod);
                throw new AuthenticationException();
            }
            
            if (agent.matches(pattern) || clientIp.matches(pattern)) {
                logger.debug("Current user agent [{}] at [{}] matches the provided pattern {} for adaptive authentication and is required to use [{}]",
                            agent, clientIp, pattern, mfaMethod);

                return buildEvent(context, service, authentication, providerFound.get());
            }
                        
            if (this.geoLocationService != null) {
                final GeoLocationRequest location = WebUtils.getHttpServletRequestGeoLocation();
                final GeoLocationResponse loc = this.geoLocationService.locate(clientIp, location);
                if (loc != null) {
                    final String address = loc.buildAddress();
                    if (address.matches(pattern)) {
                        logger.debug("Current address [{}] at [{}] matches the provided pattern {} for adaptive authentication and is required to use [{}]",
                                address, clientIp, pattern, mfaMethod);
                        return buildEvent(context, service, authentication, providerFound.get());
                    }
                }
            }
        }
        return null;
    }

    private Set<Event> buildEvent(final RequestContext context, final RegisteredService service, 
                                  final Authentication authentication, 
                                  final MultifactorAuthenticationProvider provider) {
        if (provider.isAvailable(service)) {
            logger.debug("Attempting to build an event based on the authentication provider [{}] and service [{}]", provider, service.getName());
            final Event event = validateEventIdForMatchingTransitionInContext(provider.getId(), context,
                    buildEventAttributeMap(authentication.getPrincipal(), service, provider));
            return new HashSet<>(Collections.singletonList(event));
        }
        logger.warn("Located multifactor provider [{}], yet the provider cannot be reached or verified", provider);
        return null;
    }

    @Audit(action = "AUTHENTICATION_EVENT", actionResolverName = "AUTHENTICATION_EVENT_ACTION_RESOLVER",
            resourceResolverName = "AUTHENTICATION_EVENT_RESOURCE_RESOLVER")
    @Override
    public Event resolveSingle(final RequestContext context) {
        return super.resolveSingle(context);
    }

    public void setGeoLocationService(final GeoLocationService geoLocationService) {
        this.geoLocationService = geoLocationService;
    }
}
