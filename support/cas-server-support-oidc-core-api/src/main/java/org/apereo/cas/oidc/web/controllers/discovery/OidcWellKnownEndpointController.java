package org.apereo.cas.oidc.web.controllers.discovery;

import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.oidc.OidcConstants;
import org.apereo.cas.oidc.discovery.OidcServerDiscoverySettings;
import org.apereo.cas.oidc.discovery.webfinger.OidcWebFingerDiscoveryService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.oauth.profile.OAuth20ProfileScopeToAttributesFilter;
import org.apereo.cas.support.oauth.web.endpoints.BaseOAuth20Controller;
import org.apereo.cas.ticket.accesstoken.AccessTokenFactory;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.support.gen.CookieRetrievingCookieGenerator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This is {@link OidcWellKnownEndpointController}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
public class OidcWellKnownEndpointController extends BaseOAuth20Controller {

    private final OidcServerDiscoverySettings discovery;
    private final OidcWebFingerDiscoveryService webFingerDiscoveryService;

    public OidcWellKnownEndpointController(final ServicesManager servicesManager,
                                           final TicketRegistry ticketRegistry,
                                           final AccessTokenFactory accessTokenFactory,
                                           final PrincipalFactory principalFactory,
                                           final ServiceFactory<WebApplicationService> webApplicationServiceServiceFactory,
                                           final OidcServerDiscoverySettings discovery,
                                           final OAuth20ProfileScopeToAttributesFilter scopeToAttributesFilter,
                                           final CasConfigurationProperties casProperties,
                                           final CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator,
                                           final OidcWebFingerDiscoveryService webFingerDiscoveryService) {
        super(servicesManager, ticketRegistry, accessTokenFactory,
            principalFactory, webApplicationServiceServiceFactory,
            scopeToAttributesFilter, casProperties, ticketGrantingTicketCookieGenerator);
        this.discovery = discovery;
        this.webFingerDiscoveryService = webFingerDiscoveryService;
    }

    /**
     * Gets well known discovery configuration.
     *
     * @return the well known discovery configuration
     */
    @GetMapping(value = '/' + OidcConstants.BASE_OIDC_URL + "/.well-known", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OidcServerDiscoverySettings> getWellKnownDiscoveryConfiguration() {
        return new ResponseEntity(this.discovery, HttpStatus.OK);
    }

    /**
     * Gets well known openid discovery configuration.
     *
     * @return the well known discovery configuration
     */
    @GetMapping(value = '/' + OidcConstants.BASE_OIDC_URL + "/.well-known/openid-configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OidcServerDiscoverySettings> getWellKnownOpenIdDiscoveryConfiguration() {
        return getWellKnownDiscoveryConfiguration();
    }

    /**
     * Gets web finger response.
     *
     * @param resource the resource
     * @param rel      the rel
     * @return the web finger response
     */
    @GetMapping(value = '/' + OidcConstants.BASE_OIDC_URL + "/.well-known/webfinger", produces = "application/jrd+json")
    public ResponseEntity getWebFingerResponse(@RequestParam("resource") final String resource,
                                               @RequestParam(value = "rel", required = false) final String rel) {
        return webFingerDiscoveryService.handleWebFingerDiscoveryRequest(resource, rel);
    }
}
