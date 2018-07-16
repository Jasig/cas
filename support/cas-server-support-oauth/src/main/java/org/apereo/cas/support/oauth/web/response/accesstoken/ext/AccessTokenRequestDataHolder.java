package org.apereo.cas.support.oauth.web.response.accesstoken.ext;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.support.oauth.OAuth20GrantTypes;
import org.apereo.cas.support.oauth.OAuth20ResponseTypes;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.ticket.OAuthToken;
import org.apereo.cas.ticket.TicketGrantingTicket;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This is {@link AccessTokenRequestDataHolder}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@ToString
@Getter
@Builder
public class AccessTokenRequestDataHolder {

    private Service service;

    private Authentication authentication;

    private OAuthToken token;

    private boolean generateRefreshToken;

    private OAuthRegisteredService registeredService;

    private TicketGrantingTicket ticketGrantingTicket;

    private OAuth20GrantTypes grantType;

    @Builder.Default
    private Set<String> scopes = new LinkedHashSet<>();

    private OAuth20ResponseTypes responseType;

    private String deviceCode;
}
