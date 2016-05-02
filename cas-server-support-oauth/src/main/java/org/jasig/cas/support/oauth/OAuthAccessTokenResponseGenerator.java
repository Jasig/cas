package org.jasig.cas.support.oauth;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.support.oauth.services.OAuthRegisteredService;
import org.jasig.cas.support.oauth.ticket.accesstoken.AccessToken;
import org.jasig.cas.support.oauth.ticket.refreshtoken.RefreshToken;
import org.jasig.cas.support.oauth.util.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is {@link OAuthAccessTokenResponseGenerator}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@RefreshScope
@Component("oauthAccessTokenResponseGenerator")
public class OAuthAccessTokenResponseGenerator implements AccessTokenResponseGenerator {
    protected transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The JSON factory.
     */
    protected final JsonFactory jsonFactory = new JsonFactory(new ObjectMapper());

    /**
     * The Resource loader.
     */
    @Autowired
    protected ResourceLoader resourceLoader;

    @Override
    public void generate(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final OAuthRegisteredService registeredService,
                         final Service service,
                         final AccessToken accessTokenId,
                         final RefreshToken refreshTokenId,
                         final long timeout) {

        if (registeredService.isJsonFormat()) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try (final JsonGenerator jsonGenerator = this.jsonFactory.createGenerator(response.getWriter())) {
                jsonGenerator.writeStartObject();
                generateJsonInternal(request, response, jsonGenerator, accessTokenId,
                        refreshTokenId, timeout, service, registeredService);
                jsonGenerator.writeEndObject();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            generateTextInternal(request, response, accessTokenId, refreshTokenId, timeout);
        }
    }

    /**
     * Generate text internal.
     *
     * @param request        the request
     * @param response       the response
     * @param accessTokenId  the access token id
     * @param refreshTokenId the refresh token id
     * @param timeout        the timeout
     */
    protected void generateTextInternal(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final AccessToken accessTokenId,
                                        final RefreshToken refreshTokenId,
                                        final long timeout) {
        String text = String.format("%s=%s&%s=%s", OAuthConstants.ACCESS_TOKEN, accessTokenId.getId(),
                OAuthConstants.EXPIRES, timeout);
        if (refreshTokenId != null) {
            text += '&' + OAuthConstants.REFRESH_TOKEN + '=' + refreshTokenId.getId();
        }
        OAuthUtils.writeText(response, text, HttpStatus.SC_OK);
    }

    /**
     * Generate internal.
     *
     * @param request           the request
     * @param response          the response
     * @param jsonGenerator     the json generator
     * @param accessTokenId     the access token id
     * @param refreshTokenId    the refresh token id
     * @param timeout           the timeout
     * @param service           the service
     * @param registeredService the registered service
     * @throws Exception the exception
     */
    protected void generateJsonInternal(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final JsonGenerator jsonGenerator,
                                        final AccessToken accessTokenId,
                                        final RefreshToken refreshTokenId,
                                        final long timeout,
                                        final Service service,
                                        final OAuthRegisteredService registeredService) throws Exception {
        jsonGenerator.writeStringField(OAuthConstants.ACCESS_TOKEN, accessTokenId.getId());
        jsonGenerator.writeNumberField(OAuthConstants.EXPIRES, timeout);
        if (refreshTokenId != null) {
            jsonGenerator.writeStringField(OAuthConstants.REFRESH_TOKEN, refreshTokenId.getId());
        }
    }
}
