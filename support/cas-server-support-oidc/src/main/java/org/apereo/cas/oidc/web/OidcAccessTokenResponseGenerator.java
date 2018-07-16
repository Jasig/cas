package org.apereo.cas.oidc.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.oidc.OidcConstants;
import org.apereo.cas.oidc.token.OidcIdTokenGeneratorService;
import org.apereo.cas.services.OidcRegisteredService;
import org.apereo.cas.support.oauth.web.response.accesstoken.response.OAuth20AccessTokenResponseResult;
import org.apereo.cas.support.oauth.web.response.accesstoken.response.OAuth20DefaultAccessTokenResponseGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * This is {@link OidcAccessTokenResponseGenerator}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class OidcAccessTokenResponseGenerator extends OAuth20DefaultAccessTokenResponseGenerator {
    private final OidcIdTokenGeneratorService idTokenGenerator;

    @Override
    protected Map getAccessTokenResponseModel(final HttpServletRequest request, final HttpServletResponse response, final OAuth20AccessTokenResponseResult result) {
        val model = super.getAccessTokenResponseModel(request, response, result);
        val oidcRegisteredService = (OidcRegisteredService) result.getRegisteredService();
        val idToken = this.idTokenGenerator.generate(request, response,
            result.getGeneratedToken().getAccessToken().get(),
            result.getAccessTokenTimeout(), result.getResponseType(), oidcRegisteredService);
        model.put(OidcConstants.ID_TOKEN, idToken);
        return model;
    }
}

