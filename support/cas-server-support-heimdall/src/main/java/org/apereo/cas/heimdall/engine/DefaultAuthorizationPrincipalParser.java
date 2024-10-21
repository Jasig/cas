package org.apereo.cas.heimdall.engine;

import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.support.Beans;
import org.apereo.cas.support.oauth.OAuth20Constants;
import org.apereo.cas.support.oauth.web.response.accesstoken.response.OAuth20JwtAccessTokenEncoder;
import org.apereo.cas.ticket.OAuth20TokenSigningAndEncryptionService;
import org.apereo.cas.ticket.accesstoken.OAuth20AccessToken;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.token.JwtBuilder;
import org.apereo.cas.util.function.FunctionUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

/**
 * This is {@link DefaultAuthorizationPrincipalParser}.
 *
 * @author Misagh Moayyed
 * @since 7.2.0
 */
@RequiredArgsConstructor
@Slf4j
public class DefaultAuthorizationPrincipalParser implements AuthorizationPrincipalParser {
    protected final TicketRegistry ticketRegistry;
    protected final CasConfigurationProperties casProperties;
    protected final ObjectProvider<JwtBuilder> accessTokenJwtBuilder;
    protected final ObjectProvider<OAuth20TokenSigningAndEncryptionService> oidcTokenSigningAndEncryptionService;

    @Override
    public Principal parse(final String authorizationHeader) throws Throwable {
        val claims = parseAuthorizationHeader(authorizationHeader);
        val principalAttributes = new HashMap(claims.getClaims());
        principalAttributes.put(HttpHeaders.AUTHORIZATION, authorizationHeader);
        return PrincipalFactoryUtils.newPrincipalFactory()
            .createPrincipal(claims.getSubject(), principalAttributes);
    }

    protected JWTClaimsSet parseAuthorizationHeader(final String authorizationHeader) throws Throwable {
        val token = StringUtils.removeStart(authorizationHeader, "Bearer ");
        val claims = parseOidcIdToken(token)
            .or(() -> parseJwtAccessToken(token))
            .or(() -> getJwtClaimsSetFromAccessToken(token))
            .orElseThrow(() -> new AuthenticationException("Unable to parse token"));
        return validateClaims(claims);
    }

    protected JWTClaimsSet validateClaims(final JWTClaimsSet claimsSet) {
        val maxClockSkew = Beans.newDuration(casProperties.getAuthn().getOidc().getCore().getSkew()).toSeconds();
        val now = new Date();
        val exp = claimsSet.getExpirationTime();
        if (exp != null && !DateUtils.isAfter(exp, now, maxClockSkew)) {
            throw new AuthenticationException("Token has expired: %s and is after %s".formatted(exp, now));
        }
        val nbf = claimsSet.getNotBeforeTime();
        if (nbf != null && !DateUtils.isBefore(nbf, now, maxClockSkew)) {
            throw new AuthenticationException("Token cannot be used before %s and now is %s".formatted(nbf, now));
        }
        return claimsSet;
    }

    private Optional<JWTClaimsSet> getJwtClaimsSetFromAccessToken(final String token) {
        try {
            val ticket = ticketRegistry.getTicket(token, OAuth20AccessToken.class);
            FunctionUtils.throwIf(ticket == null || ticket.isExpired(),
                () -> new AuthenticationException("Token %s is not found or has expired".formatted(token)));
            val claimsMap = new HashMap<String, Object>(ticket.getClaims());
            val authentication = ticket.getAuthentication();
            claimsMap.putAll(authentication.getAttributes());
            claimsMap.putAll(authentication.getPrincipal().getAttributes());
            claimsMap.put(OAuth20Constants.SCOPE, ticket.getScopes());
            claimsMap.put(OAuth20Constants.TOKEN, token);
            claimsMap.put(OAuth20Constants.CLAIM_SUB, authentication.getPrincipal().getId());
            return Optional.of(JWTClaimsSet.parse(claimsMap));
        } catch (final Throwable e) {
            LOGGER.debug(e.getMessage(), LOGGER.isTraceEnabled() ? e : null);
            return Optional.empty();
        }
    }

    protected Optional<JWTClaimsSet> parseJwtAccessToken(final String token) {
        try {
            return accessTokenJwtBuilder
                .stream()
                .map(builder -> {
                    val decodableCipher = OAuth20JwtAccessTokenEncoder.toDecodableCipher(builder);
                    val accessTokenId = decodableCipher.decode(token);
                    return getJwtClaimsSetFromAccessToken(accessTokenId);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        } catch (final Exception e) {
            LOGGER.debug(e.getMessage(), LOGGER.isTraceEnabled() ? e : null);
            return Optional.empty();
        }
    }

    protected Optional<JWTClaimsSet> parseOidcIdToken(final String token) {
        try {
            return oidcTokenSigningAndEncryptionService
                .stream()
                .map(service -> service.decode(token, Optional.empty()))
                .map(Unchecked.function(claims -> JWTClaimsSet.parse(claims.getClaimsMap())))
                .findFirst();
        } catch (final Exception e) {
            LOGGER.debug(e.getMessage(), LOGGER.isTraceEnabled() ? e : null);
            return Optional.empty();
        }
    }
}
